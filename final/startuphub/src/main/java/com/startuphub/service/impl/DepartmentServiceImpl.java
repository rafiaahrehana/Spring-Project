package com.startuphub.service.impl;

import com.startuphub.dto.request.DepartmentRequest;
import com.startuphub.dto.response.DepartmentResponse;
import com.startuphub.entity.Department;
import com.startuphub.entity.Employee;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.DepartmentMapper;
import com.startuphub.repository.DepartmentRepository;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository   employeeRepository;
    private final SecurityUtil         securityUtil;

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        Long companyId = requireCompanyId();

        if (departmentRepository.existsByCompanyIdAndName(companyId, request.name())) {
            throw new BadRequestException("A department named '" + request.name() + "' already exists");
        }

        Department dept = Department.builder()
            .name(request.name())
            .description(request.description())
            .company(companyRef(companyId))
            .build();

        if (request.headEmployeeId() != null) {
            Employee head = findEmployeeInTenant(request.headEmployeeId(), companyId);
            dept.setHead(head);
        }

        departmentRepository.save(dept);
        log.info("Department created: '{}' company={}", dept.getName(), companyId);
        return DepartmentMapper.toResponse(dept);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {
        return DepartmentMapper.toResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> listAll(Pageable pageable) {
        return departmentRepository
            .findByCompanyId(requireCompanyId(), pageable)
            .map(DepartmentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> listActive() {
        return departmentRepository
            .findByCompanyIdAndActiveTrue(requireCompanyId())
            .stream()
            .map(DepartmentMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Long companyId = requireCompanyId();
        Department dept = findInTenant(id);

        if (!dept.getName().equals(request.name())
                && departmentRepository.existsByCompanyIdAndName(companyId, request.name())) {
            throw new BadRequestException("A department named '" + request.name() + "' already exists");
        }

        dept.setName(request.name());
        if (request.description() != null) dept.setDescription(request.description());

        if (request.headEmployeeId() != null) {
            dept.setHead(findEmployeeInTenant(request.headEmployeeId(), companyId));
        } else {
            dept.setHead(null);
        }

        return DepartmentMapper.toResponse(dept);
    }

    @Override
    @Transactional
    public DepartmentResponse toggleActive(Long id) {
        Department dept = findInTenant(id);
        dept.setActive(!dept.isActive());
        return DepartmentMapper.toResponse(dept);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Department dept = findInTenant(id);
        if (!dept.getEmployees().isEmpty()) {
            throw new BadRequestException(
                "Cannot delete a department that has employees. Reassign employees first.");
        }
        dept.softDelete();
        log.info("Department soft-deleted: id={}", id);
    }

    // ── Private helpers ───────────────────────────────────────────

    private Department findInTenant(Long id) {
        Long companyId = requireCompanyId();
        return departmentRepository.findByIdAndCompanyId(id, companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }

    private Employee findEmployeeInTenant(Long employeeId, Long companyId) {
        return employeeRepository.findByIdAndCompanyId(employeeId, companyId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Employee not found: " + employeeId));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }

    private com.startuphub.entity.Company companyRef(Long companyId) {
        com.startuphub.entity.Company c = new com.startuphub.entity.Company();
        c.setId(companyId);
        return c;
    }
}
