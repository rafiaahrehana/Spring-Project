package com.startuphub.dto.request;

import com.startuphub.enums.ServiceRequestPriority;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateServiceRequestRequest(

    @NotBlank(message = "Request title is required")
    @Size(max = 255)
    String title,

    String description,

    @NotNull(message = "Service ID is required")
    Long hubServiceId,

    ServiceRequestPriority priority,

    @DecimalMin(value = "0.00")
    BigDecimal agreedPrice,

    LocalDateTime slaDeadline
) {}
