package com.startuphub.dto.request;

import com.startuphub.enums.LeaveType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LeaveRequestDto(

    @NotNull(message = "Leave type is required")
    LeaveType leaveType,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate,

    String reason
) {}
