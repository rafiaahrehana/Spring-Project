package com.startuphub.service;

import com.startuphub.dto.request.JobApplicationRequest;
import com.startuphub.dto.response.JobApplicationResponse;
import com.startuphub.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruitmentService {

    JobApplicationResponse apply(Long jobPostingId, JobApplicationRequest request);

    JobApplicationResponse getById(Long id);

    Page<JobApplicationResponse> listByPosting(Long jobPostingId, Pageable pageable);

    Page<JobApplicationResponse> listAll(ApplicationStatus status, Pageable pageable);

    JobApplicationResponse updateStatus(Long id, ApplicationStatus status, String notes);

    void delete(Long id);
}
