package com.startuphub.service.impl;

import com.startuphub.dto.request.AssetRequest;
import com.startuphub.dto.response.AssetResponse;
import com.startuphub.entity.Asset;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.enums.AssetStatus;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.HrmMapper;
import com.startuphub.repository.AssetRepository;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetServiceImpl implements AssetService {

    private final AssetRepository    assetRepository;
    private final EmployeeRepository employeeRepository;
    private final SecurityUtil       securityUtil;

    @Override
    @Transactional
    public AssetResponse create(AssetRequest request) {
        Long companyId = requireCompanyId();
        Asset asset = Asset.builder()
            .name(request.name())
            .category(request.category())
            .serialNumber(request.serialNumber())
            .description(request.description())
            .purchaseDate(request.purchaseDate())
            .purchaseCost(request.purchaseCost())
            .notes(request.notes())
            .status(AssetStatus.AVAILABLE)
            .company(companyRef(companyId))
            .build();

        if (request.assignedToId() != null) {
            Employee emp = findEmployee(request.assignedToId(), companyId);
            asset.setAssignedTo(emp);
            asset.setStatus(AssetStatus.ASSIGNED);
            asset.setAssignedAt(LocalDate.now());
        }

        assetRepository.save(asset);
        return HrmMapper.toAssetResponse(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getById(Long id) {
        return HrmMapper.toAssetResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> listAll(AssetStatus status, Pageable pageable) {
        Long companyId = requireCompanyId();
        Page<Asset> page = status != null
            ? assetRepository.findByCompanyIdAndStatus(companyId, status, pageable)
            : assetRepository.findByCompanyId(companyId, pageable);
        return page.map(HrmMapper::toAssetResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponse> listForEmployee(Long employeeId) {
        return assetRepository.findByCompanyIdAndAssignedToId(requireCompanyId(), employeeId)
            .stream().map(HrmMapper::toAssetResponse).toList();
    }

    @Override
    @Transactional
    public AssetResponse update(Long id, AssetRequest request) {
        Long companyId = requireCompanyId();
        Asset asset = findInTenant(id);
        if (request.name()         != null) asset.setName(request.name());
        if (request.category()     != null) asset.setCategory(request.category());
        if (request.serialNumber() != null) asset.setSerialNumber(request.serialNumber());
        if (request.description()  != null) asset.setDescription(request.description());
        if (request.purchaseDate() != null) asset.setPurchaseDate(request.purchaseDate());
        if (request.purchaseCost() != null) asset.setPurchaseCost(request.purchaseCost());
        if (request.notes()        != null) asset.setNotes(request.notes());
        return HrmMapper.toAssetResponse(asset);
    }

    @Override
    @Transactional
    public AssetResponse assign(Long id, Long employeeId) {
        Long companyId = requireCompanyId();
        Asset asset = findInTenant(id);
        if (asset.getStatus() == AssetStatus.ASSIGNED) {
            throw new BadRequestException("Asset is already assigned. Unassign it first.");
        }
        Employee emp = findEmployee(employeeId, companyId);
        asset.setAssignedTo(emp);
        asset.setStatus(AssetStatus.ASSIGNED);
        asset.setAssignedAt(LocalDate.now());
        asset.setReturnDate(null);
        return HrmMapper.toAssetResponse(asset);
    }

    @Override
    @Transactional
    public AssetResponse unassign(Long id) {
        Asset asset = findInTenant(id);
        if (asset.getStatus() != AssetStatus.ASSIGNED) {
            throw new BadRequestException("Asset is not currently assigned");
        }
        asset.setAssignedTo(null);
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setReturnDate(LocalDate.now());
        return HrmMapper.toAssetResponse(asset);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Asset asset = findInTenant(id);
        if (asset.getStatus() == AssetStatus.ASSIGNED) {
            throw new BadRequestException("Cannot delete an assigned asset. Unassign it first.");
        }
        asset.softDelete();
    }

    private Asset findInTenant(Long id) {
        return assetRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + id));
    }

    private Employee findEmployee(Long employeeId, Long companyId) {
        return employeeRepository.findByIdAndCompanyId(employeeId, companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }

    private Company companyRef(Long companyId) {
        Company c = new Company(); c.setId(companyId); return c;
    }
}
