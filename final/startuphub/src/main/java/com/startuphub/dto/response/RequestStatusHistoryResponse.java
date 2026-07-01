package com.startuphub.dto.response;

import com.startuphub.enums.ServiceRequestStatus;

import java.time.LocalDateTime;

public record RequestStatusHistoryResponse(
    Long id,
    ServiceRequestStatus oldStatus,
    ServiceRequestStatus newStatus,
    String reason,
    Long changedById,
    String changedByName,
    LocalDateTime changedAt
) {}
