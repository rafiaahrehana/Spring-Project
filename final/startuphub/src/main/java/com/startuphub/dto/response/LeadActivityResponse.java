package com.startuphub.dto.response;

import com.startuphub.enums.LeadActivityType;

import java.time.LocalDateTime;

public record LeadActivityResponse(
    Long id,
    LeadActivityType activityType,
    String subject,
    String description,
    LocalDateTime activityAt,
    LocalDateTime nextFollowUp,
    Long leadId,
    Long createdById,
    String createdByName,
    LocalDateTime createdAt
) {}
