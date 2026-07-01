package com.startuphub.dto.response;

import java.time.LocalDateTime;

public record AnnouncementResponse(
    Long id,
    String title,
    String body,
    LocalDateTime publishedAt,
    LocalDateTime expiresAt,
    boolean published,
    boolean notifyAll,
    Long createdById,
    String createdByName,
    LocalDateTime createdAt
) {}
