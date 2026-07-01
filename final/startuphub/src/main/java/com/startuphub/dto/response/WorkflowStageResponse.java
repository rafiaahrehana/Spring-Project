package com.startuphub.dto.response;

import com.startuphub.enums.Role;

public record WorkflowStageResponse(
    Long id,
    String name,
    int stageOrder,
    Integer estimatedDays,
    Integer slaHours,
    boolean requiresApproval,
    Role assigneeRole,
    String description
) {}
