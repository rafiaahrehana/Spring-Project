package com.StartupSAAS.dto.request;

import lombok.Data;

@Data
public class TaskStatusDTO {
    // e.g. "TODO", "IN_PROGRESS", "REVIEW", "DONE"
    private String status;
    private String notes;
}
