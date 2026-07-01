package com.startuphub.service.impl;

import com.startuphub.dto.request.EmploymentLetterRequest;
import com.startuphub.dto.response.EmploymentLetterResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.entity.EmploymentLetter;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.Phase8Mapper;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.EmploymentLetterRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.EmploymentLetterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmploymentLetterServiceImpl implements EmploymentLetterService {

    private final EmploymentLetterRepository letterRepository;
    private final EmployeeRepository         employeeRepository;
    private final SecurityUtil               securityUtil;

    @Override
    @Transactional
    public EmploymentLetterResponse create(EmploymentLetterRequest request) {
        Long companyId = requireCompanyId();
        Employee employee = employeeRepository.findByIdAndCompanyId(request.employeeId(), companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + request.employeeId()));

        if (request.referenceNumber() != null
                && letterRepository.existsByCompanyIdAndReferenceNumber(companyId, request.referenceNumber())) {
            throw new BadRequestException("Reference number already exists: " + request.referenceNumber());
        }

        EmploymentLetter letter = EmploymentLetter.builder()
            .employee(employee)
            .company(companyRef(companyId))
            .letterType(request.letterType())
            .referenceNumber(request.referenceNumber())
            .issueDate(request.issueDate())
            .content(request.content())
            .signedBy(request.signedBy())
            .createdBy(securityUtil.getCurrentUser())
            .issued(false)
            .build();

        letterRepository.save(letter);
        return Phase8Mapper.toLetterResponse(letter);
    }

    @Override
    @Transactional(readOnly = true)
    public EmploymentLetterResponse getById(Long id) {
        return Phase8Mapper.toLetterResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmploymentLetterResponse> listAll(Pageable pageable) {
        return letterRepository.findByCompanyId(requireCompanyId(), pageable)
            .map(Phase8Mapper::toLetterResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmploymentLetterResponse> listForEmployee(Long employeeId, Pageable pageable) {
        return letterRepository.findByCompanyIdAndEmployeeId(requireCompanyId(), employeeId, pageable)
            .map(Phase8Mapper::toLetterResponse);
    }

    @Override
    @Transactional
    public EmploymentLetterResponse issue(Long id) {
        EmploymentLetter letter = findInTenant(id);
        if (letter.isIssued()) throw new BadRequestException("Letter is already issued");
        letter.setIssued(true);
        return Phase8Mapper.toLetterResponse(letter);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        EmploymentLetter letter = findInTenant(id);
        if (letter.isIssued()) throw new BadRequestException("Cannot delete an issued letter");
        letter.softDelete();
    }

    private EmploymentLetter findInTenant(Long id) {
        return letterRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Employment letter not found: " + id));
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
