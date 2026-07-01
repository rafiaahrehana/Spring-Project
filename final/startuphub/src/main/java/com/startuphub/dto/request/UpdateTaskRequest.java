package com.startuphub.dto.request;

import com.startuphub.enums.ServiceRequestPriority;
import com.startuphub.enums.TaskStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateTaskRequest(

    @Size(max = 255)
    String title,

    String description,

    TaskStatus status,

    ServiceRequestPriority priority,

    LocalDateTime dueDate,

    LocalDateTime slaDeadline,

    Long assignedEmployeeId
) {}
