package com.startuphub.dto.response;

import com.startuphub.enums.ServicePriceType;
import com.startuphub.enums.ServiceRequestPriority;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HubServiceResponse(
    Long id,
    String name,
    String nameBn,
    String description,
    String descriptionBn,
    BigDecimal price,
    ServicePriceType priceType,
    Integer estimatedDays,
    ServiceRequestPriority defaultPriority,
    boolean active,
    Long categoryId,
    String categoryName,
    Long workflowTemplateId,
    String workflowTemplateName,
    LocalDateTime createdAt
) {}
