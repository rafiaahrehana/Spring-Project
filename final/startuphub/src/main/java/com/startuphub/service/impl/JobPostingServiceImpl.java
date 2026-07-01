package com.startuphub.service.impl;

import com.startuphub.dto.request.JobPostingRequest;
import com.startuphub.dto.response.JobPostingResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Department;
import com.startuphub.entity.Employee;
import com.startuphub.entity.JobPosting;
import com.startuphub.entity.User;
import com.startuphub.enums.EmploymentType;
import com.startuphub.enums.JobPostingStatus;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.JobPostingMapper;
import com.startuphub.repository.DepartmentRepository;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.JobPostingRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final EmployeeRepository   employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final SecurityUtil         securityUtil;

    @Override
    @Transactional
    public JobPostingResponse create(JobPostingRequest request) {
        Long companyId = requireCompanyId();
        User currentUser = securityUtil.getCurrentUser();

        Employee creator = employeeRepository.findByUserId(currentUser.getId())
            .orElseThrow(() -> new BadRequestException(
                "Only employees can create job postings"));

        JobPosting posting = JobPosting.builder()
            .title(request.title())
            .jobTitle(request.jobTitle())
            .description(request.description())
            .requirements(request.requirements())
            .employmentType(request.employmentType())
            .status(request.status() != null ? request.status() : JobPostingStatus.DRAFT)
            .vacancies(request.vacancies() != null ? request.vacancies() : 1)
            .salaryMin(request.salaryMin())
            .salaryMax(request.salaryMax())
            .deadline(request.deadline())
            .remote(request.remote())
            .company(companyRef(companyId))
            .createdBy(creator)
            .build();

        if (request.departmentId() != null) {
            Department dept = departmentRepository
                .findByIdAndCompanyId(request.departmentId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Department not found: " + request.departmentId()));
            posting.setDepartment(dept);
        }

        jobPostingRepository.save(posting);
        log.info("JobPosting created: '{}' company={}", posting.getTitle(), companyId);
        return JobPostingMapper.toResponse(posting);
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostingResponse getById(Long id) {
        return JobPostingMapper.toResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobPostingResponse> listAll(JobPostingStatus status, Pageable pageable) {
        Long companyId = requireCompanyId();
        Page<JobPosting> page = status != null
            ? jobPostingRepository.findByCompanyIdAndStatus(companyId, status, pageable)
            : jobPostingRepository.findByCompanyId(companyId, pageable);
        return page.map(JobPostingMapper::toResponse);
    }

    @Override
    @Transactional
    public JobPostingResponse update(Long id, JobPostingRequest request) {
        Long companyId = requireCompanyId();
        JobPosting posting = findInTenant(id);

        if (posting.getStatus() == JobPostingStatus.CLOSED) {
            throw new BadRequestException("Cannot edit a closed job posting");
        }

        if (request.title()        != null) posting.setTitle(request.title());
        if (request.jobTitle()     != null) posting.setJobTitle(request.jobTitle());
        if (request.description()  != null) posting.setDescription(request.description());
        if (request.requirements() != null) posting.setRequirements(request.requirements());
        if (request.employmentType()!= null) posting.setEmploymentType(request.employmentType());
        if (request.vacancies()    != null) posting.setVacancies(request.vacancies());
        if (request.salaryMin()    != null) posting.setSalaryMin(request.salaryMin());
        if (request.salaryMax()    != null) posting.setSalaryMax(request.salaryMax());
        if (request.deadline()     != null) posting.setDeadline(request.deadline());
        posting.setRemote(request.remote());

        if (request.departmentId() != null) {
            posting.setDepartment(departmentRepository
                .findByIdAndCompanyId(request.departmentId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Department not found: " + request.departmentId())));
        }

        return JobPostingMapper.toResponse(posting);
    }

    @Override
    @Transactional
    public JobPostingResponse publish(Long id) {
        JobPosting posting = findInTenant(id);
        if (posting.getStatus() == JobPostingStatus.CLOSED) {
            throw new BadRequestException("Cannot publish a closed job posting");
        }
        posting.setStatus(JobPostingStatus.OPEN);
        log.info("JobPosting published: id={}", id);
        return JobPostingMapper.toResponse(posting);
    }

    @Override
    @Transactional
    public JobPostingResponse close(Long id) {
        JobPosting posting = findInTenant(id);
        posting.setStatus(JobPostingStatus.CLOSED);
        log.info("JobPosting closed: id={}", id);
        return JobPostingMapper.toResponse(posting);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        JobPosting posting = findInTenant(id);
        posting.softDelete();
        log.info("JobPosting soft-deleted: id={}", id);
    }

    // ── Private helpers ───────────────────────────────────────────

    private JobPosting findInTenant(Long id) {
        return jobPostingRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Job posting not found: " + id));
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
