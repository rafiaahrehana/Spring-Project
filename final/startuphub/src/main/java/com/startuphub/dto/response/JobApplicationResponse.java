package com.startuphub.dto.response;

import com.startuphub.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record JobApplicationResponse(
    Long id,
    String applicantName,
    String applicantEmail,
    String applicantPhone,
    String resumeUrl,
    String coverLetter,
    ApplicationStatus status,
    String notes,
    Long jobPostingId,
    String jobPostingTitle,
    Long reviewedById,
    String reviewedByName,
    LocalDateTime createdAt
) {}
