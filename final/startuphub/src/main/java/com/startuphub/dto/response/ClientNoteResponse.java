package com.startuphub.dto.response;

import java.time.LocalDateTime;

public record ClientNoteResponse(
    Long id,
    String content,
    LocalDateTime followUpAt,
    Long clientId,
    Long createdById,
    String createdByName,
    LocalDateTime createdAt
) {}
