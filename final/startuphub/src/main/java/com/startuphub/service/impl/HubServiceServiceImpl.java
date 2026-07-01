package com.startuphub.service.impl;

import com.startuphub.dto.request.HubServiceRequest;
import com.startuphub.dto.response.HubServiceResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.HubService;
import com.startuphub.entity.ServiceCategory;
import com.startuphub.entity.WorkflowTemplate;
import com.startuphub.enums.ServicePriceType;
import com.startuphub.enums.ServiceRequestPriority;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.HubServiceMapper;
import com.startuphub.repository.HubServiceRepository;
import com.startuphub.repository.ServiceCategoryRepository;
import com.startuphub.repository.WorkflowTemplateRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.HubServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubServiceServiceImpl implements HubServiceService {

    private final HubServiceRepository      hubServiceRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final WorkflowTemplateRepository templateRepository;
    private final SecurityUtil              securityUtil;

    @Override
    @Transactional
    public HubServiceResponse create(HubServiceRequest request) {
        Long companyId = requireCompanyId();

        ServiceCategory category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Service category not found: " + request.categoryId()));
        }

        WorkflowTemplate workflow = null;
        if (request.workflowTemplateId() != null) {
            workflow = templateRepository
                .findByIdAndCompanyId(request.workflowTemplateId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Workflow template not found: " + request.workflowTemplateId()));
        }

        HubService service = HubService.builder()
            .name(request.name())
            .nameBn(request.nameBn())
            .description(request.description())
            .descriptionBn(request.descriptionBn())
            .price(request.price())
            .priceType(request.priceType() != null ? request.priceType() : ServicePriceType.FIXED)
            .estimatedDays(request.estimatedDays())
            .defaultPriority(request.defaultPriority() != null
                ? request.defaultPriority() : ServiceRequestPriority.NORMAL)
            .company(companyRef(companyId))
            .category(category)
            .workflowTemplate(workflow)
            .build();

        hubServiceRepository.save(service);
        log.info("HubService created: '{}' company={}", service.getName(), companyId);
        return HubServiceMapper.toResponse(service);
    }

    @Override
    @Transactional(readOnly = true)
    public HubServiceResponse getById(Long id) {
        return HubServiceMapper.toResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HubServiceResponse> listAll(Long categoryId, Pageable pageable) {
        Long companyId = requireCompanyId();
        Page<HubService> page = categoryId != null
            ? hubServiceRepository.findByCompanyIdAndCategoryId(companyId, categoryId, pageable)
            : hubServiceRepository.findByCompanyId(companyId, pageable);
        return page.map(HubServiceMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HubServiceResponse> listActive() {
        return hubServiceRepository.findByCompanyIdAndActiveTrue(requireCompanyId())
            .stream().map(HubServiceMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public HubServiceResponse update(Long id, HubServiceRequest request) {
        Long companyId = requireCompanyId();
        HubService service = findInTenant(id);

        if (request.name()        != null) service.setName(request.name());
        if (request.nameBn()      != null) service.setNameBn(request.nameBn());
        if (request.description() != null) service.setDescription(request.description());
        if (request.descriptionBn()!= null) service.setDescriptionBn(request.descriptionBn());
        if (request.price()       != null) service.setPrice(request.price());
        if (request.priceType()   != null) service.setPriceType(request.priceType());
        if (request.estimatedDays()!= null) service.setEstimatedDays(request.estimatedDays());
        if (request.defaultPriority()!= null) service.setDefaultPriority(request.defaultPriority());

        if (request.categoryId() != null) {
            service.setCategory(categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Service category not found: " + request.categoryId())));
        }
        if (request.workflowTemplateId() != null) {
            service.setWorkflowTemplate(
                templateRepository.findByIdAndCompanyId(request.workflowTemplateId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Workflow template not found: " + request.workflowTemplateId())));
        }

        return HubServiceMapper.toResponse(service);
    }

    @Override
    @Transactional
    public HubServiceResponse toggleActive(Long id) {
        HubService service = findInTenant(id);
        service.setActive(!service.isActive());
        return HubServiceMapper.toResponse(service);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        HubService service = findInTenant(id);
        service.softDelete();
        log.info("HubService soft-deleted: id={}", id);
    }

    // ── Private helpers ───────────────────────────────────────────

    private HubService findInTenant(Long id) {
        return hubServiceRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + id));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }

    private Company companyRef(Long companyId) {
        Company c = new Company();
        c.setId(companyId);
        return c;
    }
}
