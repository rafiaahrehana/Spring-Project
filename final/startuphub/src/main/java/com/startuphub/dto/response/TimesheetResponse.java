package com.startuphub.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TimesheetResponse(
    Long id,
    LocalDate workDate,
    LocalDateTime startTime,
    LocalDateTime endTime,
    double hoursWorked,
    double billableHours,
    String description,
    boolean approved,
    LocalDateTime approvedAt,
    Long employeeId,
    String employeeName,
    Long approvedById,
    String approvedByName,
    Long taskId,
    String taskTitle,
    LocalDateTime createdAt
) {}
