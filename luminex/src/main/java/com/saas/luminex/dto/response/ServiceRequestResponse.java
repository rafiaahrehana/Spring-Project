package com.saas.luminex.dto.response;

import com.saas.luminex.enums.Priority;
import com.saas.luminex.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ServiceRequestResponse {
    private Long id;
    private Long clientId;
    private String clientName;
    private Long serviceId;
    private String serviceName;
    private String categoryName;
    private Long assignedEmployeeId;
    private String assignedEmployeeName;
    private RequestStatus status;
    private Priority priority;
    private Integer progress;
    private Integer workedHours;
    private String adminNotes;
    private String clientNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
