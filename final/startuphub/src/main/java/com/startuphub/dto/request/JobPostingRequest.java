package com.startuphub.dto.request;

import com.startuphub.enums.EmploymentType;
import com.startuphub.enums.JobPostingStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record JobPostingRequest(

    @NotBlank(message = "Job posting title is required")
    @Size(max = 150)
    String title,

    @Size(max = 100)
    String jobTitle,

    String description,

    String requirements,

    EmploymentType employmentType,

    JobPostingStatus status,

    @Min(value = 1, message = "Must have at least 1 vacancy")
    Integer vacancies,

    @DecimalMin(value = "0.00")
    BigDecimal salaryMin,

    @DecimalMin(value = "0.00")
    BigDecimal salaryMax,

    LocalDate deadline,

    boolean remote,

    Long departmentId
) {}
