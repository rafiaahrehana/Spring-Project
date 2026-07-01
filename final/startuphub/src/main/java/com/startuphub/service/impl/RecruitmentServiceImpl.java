package com.startuphub.service.impl;

import com.startuphub.dto.request.JobApplicationRequest;
import com.startuphub.dto.response.JobApplicationResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.entity.JobApplication;
import com.startuphub.entity.JobPosting;
import com.startuphub.enums.ApplicationStatus;
import com.startuphub.enums.JobPostingStatus;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.Phase8Mapper;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.JobApplicationRepository;
import com.startuphub.repository.JobPostingRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitmentServiceImpl implements RecruitmentService {

    private final JobApplicationRepository applicationRepository;
    private final JobPostingRepository     jobPostingRepository;
    private final EmployeeRepository       employeeRepository;
    private final SecurityUtil             securityUtil;

    @Override
    @Transactional
    public JobApplicationResponse apply(Long jobPostingId, JobApplicationRequest request) {
        Long companyId = requireCompanyId();
        JobPosting posting = jobPostingRepository.findByIdAndCompanyId(jobPostingId, companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Job posting not found: " + jobPostingId));

        if (posting.getStatus() != JobPostingStatus.OPEN) {
            throw new BadRequestException("This position is no longer accepting applications");
        }
        if (applicationRepository.existsByJobPostingIdAndApplicantEmail(
                jobPostingId, request.applicantEmail().toLowerCase().trim())) {
            throw new BadRequestException("An application from this email already exists for this position");
        }

        JobApplication application = JobApplication.builder()
            .jobPosting(posting)
            .company(companyRef(companyId))
            .applicantName(request.applicantName())
            .applicantEmail(request.applicantEmail().toLowerCase().trim())
            .applicantPhone(request.applicantPhone())
            .resumeUrl(request.resumeUrl())
            .coverLetter(request.coverLetter())
            .status(ApplicationStatus.APPLIED)
            .build();

        applicationRepository.save(application);
        log.info("Application received: posting={} email={}", jobPostingId, request.applicantEmail());
        return Phase8Mapper.toJobApplicationResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplicationResponse getById(Long id) {
        return Phase8Mapper.toJobApplicationResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> listByPosting(Long jobPostingId, Pageable pageable) {
        return applicationRepository.findByCompanyIdAndJobPostingId(
                requireCompanyId(), jobPostingId, pageable)
            .map(Phase8Mapper::toJobApplicationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> listAll(ApplicationStatus status, Pageable pageable) {
        Long companyId = requireCompanyId();
        return (status != null
            ? applicationRepository.findByCompanyIdAndStatus(companyId, status, pageable)
            : applicationRepository.findByCompanyId(companyId, pageable))
            .map(Phase8Mapper::toJobApplicationResponse);
    }

    @Override
    @Transactional
    public JobApplicationResponse updateStatus(Long id, ApplicationStatus status, String notes) {
        Long companyId = requireCompanyId();
        JobApplication application = findInTenant(id);
        Employee reviewer = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));
        application.setStatus(status);
        if (notes != null) application.setNotes(notes);
        application.setReviewedBy(reviewer);
        return Phase8Mapper.toJobApplicationResponse(application);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findInTenant(id).softDelete();
    }

    private JobApplication findInTenant(Long id) {
        return applicationRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
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
