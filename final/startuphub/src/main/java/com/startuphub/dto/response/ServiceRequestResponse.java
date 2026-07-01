package com.startuphub.dto.response;

import com.startuphub.enums.ServiceRequestPriority;
import com.startuphub.enums.ServiceRequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceRequestResponse(
    Long id,
    String title,
    String description,
    ServiceRequestStatus status,
    ServiceRequestPriority priority,
    BigDecimal agreedPrice,
    LocalDateTime slaDeadline,
    boolean slaBreach,
    LocalDateTime assignedAt,
    LocalDateTime completedAt,
    int resubmitCount,
    boolean permanentlyClosed,
    Long companyId,
    Long clientId,
    String clientName,
    Long hubServiceId,
    String hubServiceName,
    Long assignedEmployeeId,
    String assignedEmployeeName,
    long taskCount,
    long completedTaskCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
