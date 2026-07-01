package com.startuphub.dto.response;

import com.startuphub.enums.LeadStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeadResponse(
    Long id,
    String contactName,
    String companyName,
    String email,
    String phone,
    String industry,
    String notes,
    LeadStatus status,
    BigDecimal estimatedValue,
    LocalDateTime convertedAt,
    Long assignedToId,
    String assignedToName,
    Long interestedServiceId,
    String interestedServiceName,
    Long convertedClientId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
