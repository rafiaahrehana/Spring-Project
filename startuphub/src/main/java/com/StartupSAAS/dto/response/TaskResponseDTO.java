package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.Priority;
import com.StartupSAAS.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private LocalDateTime completedAt;
    private String notes;
    private LocalDateTime createdAt;

    // Flattened service request
    private Long serviceRequestId;
    private String serviceRequestTitle;
    private String serviceRequestStatus;

    // Flattened assigned employee
    private Long assignedEmployeeId;
    private String assignedEmployeeFirstName;
    private String assignedEmployeeLastName;
    private String assignedEmployeeDesignation;

    // Flattened created by
    private Long createdById;
    private String createdByFirstName;
    private String createdByLastName;
}
