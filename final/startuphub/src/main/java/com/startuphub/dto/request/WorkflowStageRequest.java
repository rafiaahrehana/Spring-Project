package com.startuphub.dto.request;

import com.startuphub.enums.Role;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record WorkflowStageRequest(

    @NotBlank(message = "Stage name is required")
    @Size(max = 100)
    String name,

    @NotNull(message = "Stage order is required")
    @Min(value = 1, message = "Stage order must be at least 1")
    Integer stageOrder,

    Integer estimatedDays,

    Integer slaHours,

    boolean requiresApproval,

    Role assigneeRole,

    String description
) {}
