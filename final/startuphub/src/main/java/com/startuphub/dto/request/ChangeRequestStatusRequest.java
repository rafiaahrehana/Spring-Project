package com.startuphub.dto.request;

import com.startuphub.enums.ServiceRequestStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeRequestStatusRequest(

    @NotNull(message = "New status is required")
    ServiceRequestStatus status,

    String reason
) {}
