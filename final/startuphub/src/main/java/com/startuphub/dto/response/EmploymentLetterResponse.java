package com.startuphub.dto.response;

import com.startuphub.enums.LetterType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmploymentLetterResponse(
    Long id,
    LetterType letterType,
    String referenceNumber,
    LocalDate issueDate,
    String content,
    String signedBy,
    String fileUrl,
    boolean issued,
    Long employeeId,
    String employeeName,
    Long createdById,
    String createdByName,
    LocalDateTime createdAt
) {}
