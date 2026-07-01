package com.startuphub.dto.response;

import com.startuphub.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    NotificationType type,
    String title,
    String message,
    String actionUrl,
    boolean read,
    LocalDateTime readAt,
    Long serviceRequestId,
    LocalDateTime createdAt
) {}
