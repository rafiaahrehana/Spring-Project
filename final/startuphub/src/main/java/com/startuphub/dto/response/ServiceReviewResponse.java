package com.startuphub.dto.response;

import java.time.LocalDateTime;

public record ServiceReviewResponse(
    Long id,
    int rating,
    String comment,
    boolean published,
    Long serviceRequestId,
    Long hubServiceId,
    String hubServiceName,
    Long clientId,
    String clientName,
    LocalDateTime createdAt
) {}
