package com.startuphub.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TimesheetRequest(

    @NotNull(message = "Work date is required")
    LocalDate workDate,

    LocalDateTime startTime,

    LocalDateTime endTime,

    @NotNull(message = "Hours worked is required")
    @Min(value = 0)
    Double hoursWorked,

    Double billableHours,

    String description,

    Long taskId
) {}
