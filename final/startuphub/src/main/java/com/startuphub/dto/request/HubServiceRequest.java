package com.startuphub.dto.request;

import com.startuphub.enums.ServicePriceType;
import com.startuphub.enums.ServiceRequestPriority;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record HubServiceRequest(

    @NotBlank(message = "Service name is required")
    @Size(max = 150)
    String name,

    @Size(max = 150)
    String nameBn,

    String description,

    String descriptionBn,

    @DecimalMin(value = "0.00", message = "Price must be zero or positive")
    BigDecimal price,

    ServicePriceType priceType,

    Integer estimatedDays,

    ServiceRequestPriority defaultPriority,

    Long categoryId,

    Long workflowTemplateId
) {}
