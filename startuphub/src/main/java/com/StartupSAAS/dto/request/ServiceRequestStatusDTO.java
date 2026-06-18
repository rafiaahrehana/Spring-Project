package com.StartupSAAS.dto.request;

import lombok.Data;

@Data
public class ServiceRequestStatusDTO {
    // e.g. "ASSIGNED", "IN_PROGRESS", "REVIEW", "COMPLETED", "REJECTED"
    private String status;
    private Long assignedEmployeeId;
    private String notes;
}
