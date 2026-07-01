package com.startuphub.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record WorkflowTemplateResponse(
    Long id,
    String name,
    String description,
    int version,
    boolean active,
    List<WorkflowStageResponse> stages,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
