package com.startuphub.dto.request;

import com.startuphub.enums.ServiceRequestPriority;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateServiceRequestRequest(

    @Size(max = 255)
    String title,

    String description,

    ServiceRequestPriority priority,

    BigDecimal agreedPrice,

    LocalDateTime slaDeadline,

    Long assignedEmployeeId
) {}
