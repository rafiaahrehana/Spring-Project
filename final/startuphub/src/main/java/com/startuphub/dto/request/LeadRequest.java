package com.startuphub.dto.request;

import com.startuphub.enums.LeadActivityType;
import com.startuphub.enums.LeadStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeadRequest(

    @NotBlank(message = "Contact name is required")
    @Size(max = 150)
    String contactName,

    @Size(max = 150)
    String companyName,

    @Size(max = 255)
    String email,

    @Size(max = 30)
    String phone,

    @Size(max = 100)
    String industry,

    String notes,

    LeadStatus status,

    @DecimalMin(value = "0.00")
    BigDecimal estimatedValue,

    Long assignedToId,

    Long interestedServiceId
) {}
