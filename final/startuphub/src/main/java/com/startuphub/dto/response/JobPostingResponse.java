package com.startuphub.dto.response;

import com.startuphub.enums.EmploymentType;
import com.startuphub.enums.JobPostingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record JobPostingResponse(
    Long id,
    String title,
    String jobTitle,
    String description,
    String requirements,
    EmploymentType employmentType,
    JobPostingStatus status,
    int vacancies,
    BigDecimal salaryMin,
    BigDecimal salaryMax,
    LocalDate deadline,
    boolean remote,
    Long departmentId,
    String departmentName,
    Long createdById,
    String createdByName,
    LocalDateTime createdAt
) {}
