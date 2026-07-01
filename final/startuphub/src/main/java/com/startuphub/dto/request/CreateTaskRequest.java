package com.startuphub.dto.request;

import com.startuphub.enums.ServiceRequestPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTaskRequest(

    @NotBlank(message = "Task title is required")
    @Size(max = 255)
    String title,

    String description,

    ServiceRequestPriority priority,

    LocalDateTime dueDate,

    LocalDateTime slaDeadline,

    Long assignedEmployeeId,

    Long workflowStageId
) {}
