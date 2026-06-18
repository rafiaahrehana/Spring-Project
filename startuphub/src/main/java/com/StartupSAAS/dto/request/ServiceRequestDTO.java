package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.Priority;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ServiceRequestDTO {
    private String title;
    private String description;
    private Priority priority;
    private LocalDate deadline;
    private Double agreedPrice;
    private Integer slaHours;
    private Long hubServiceId;
    private Long clientId;
    private Long companyId;
    private Long assignedEmployeeId;
}
