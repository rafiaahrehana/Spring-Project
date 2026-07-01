package com.startuphub.dto.request;

import com.startuphub.enums.LeaveRequestStatus;
import jakarta.validation.constraints.NotNull;

public record ReviewLeaveRequest(

    @NotNull(message = "Status is required")
    LeaveRequestStatus status,

    String rejectionReason
) {}
