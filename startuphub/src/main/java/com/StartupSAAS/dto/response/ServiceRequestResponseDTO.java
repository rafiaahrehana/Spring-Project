package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.Priority;
import com.StartupSAAS.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ServiceRequestResponseDTO {
    private Long id;
    private String title;
    private String description;
    private RequestStatus status;
    private Priority priority;
    private LocalDate deadline;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
    private Double agreedPrice;
    private Integer currentStage;
    private Integer slaHours;
    private LocalDateTime createdAt;

    // Flattened service
    private Long hubServiceId;
    private String hubServiceName;
    private Double hubServicePrice;

    // Flattened client
    private Long clientId;
    private String clientFirstName;
    private String clientLastName;
    private String clientEmail;

    // Flattened company
    private Long companyId;
    private String companyName;

    // Flattened assigned employee
    private Long assignedEmployeeId;
    private String assignedEmployeeFirstName;
    private String assignedEmployeeLastName;
    private String assignedEmployeeDesignation;
}
