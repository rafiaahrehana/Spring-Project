package com.saas.luminex.dto.request;

import com.saas.luminex.enums.Priority;
import com.saas.luminex.enums.RequestStatus;
import lombok.Data;

@Data
public class ServiceRequestUpdateRequest {
    private RequestStatus status;
    private Priority priority;
    private Integer progress;
    private Integer workedHours;
    private Long assignedEmployeeId;
    private String adminNotes;
}
