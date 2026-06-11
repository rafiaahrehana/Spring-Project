package com.saas.luminex.dto.mapper;

import com.saas.luminex.dto.response.ServiceRequestResponse;
import com.saas.luminex.entity.ServiceRequest;

public class ServiceRequestMapper {

    public static ServiceRequestResponse toDTO(ServiceRequest r) {
        return ServiceRequestResponse.builder()
                .id(r.getId())
                .clientId(r.getClient().getId())
                .clientName(r.getClient().getName())
                .serviceId(r.getService().getId())
                .serviceName(r.getService().getName())
                .categoryName(r.getService().getCategory().getName())
                .assignedEmployeeId(r.getAssignedEmployee() != null ? r.getAssignedEmployee().getId() : null)
                .assignedEmployeeName(r.getAssignedEmployee() != null ? r.getAssignedEmployee().getName() : null)
                .status(r.getStatus())
                .priority(r.getPriority())
                .progress(r.getProgress())
                .workedHours(r.getWorkedHours())
                .adminNotes(r.getAdminNotes())
                .clientNotes(r.getClientNotes())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
