package com.startuphub.dto.response;

import com.startuphub.enums.LeaveRequestStatus;
import com.startuphub.enums.LeaveType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LeaveRequestResponse(
    Long id,
    LeaveType leaveType,
    LocalDate startDate,
    LocalDate endDate,
    int totalDays,
    String reason,
    LeaveRequestStatus status,
    String rejectionReason,
    LocalDateTime reviewedAt,
    Long employeeId,
    String employeeName,
    Long reviewedById,
    String reviewedByName,
    LocalDateTime createdAt
) {}
