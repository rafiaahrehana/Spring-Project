package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.Priority;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequestDTO {
    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private String notes;
    private Long serviceRequestId;
    private Long assignedEmployeeId;
    private Long createdById;
}
