package com.startuphub.dto.response;

import com.startuphub.enums.ServiceRequestPriority;
import com.startuphub.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    String title,
    String description,
    TaskStatus status,
    ServiceRequestPriority priority,
    LocalDateTime dueDate,
    LocalDateTime slaDeadline,
    LocalDateTime completedAt,
    Long serviceRequestId,
    Long assignedEmployeeId,
    String assignedEmployeeName,
    Long createdById,
    String createdByName,
    Long workflowStageId,
    String workflowStageName,
    LocalDateTime createdAt
) {}
