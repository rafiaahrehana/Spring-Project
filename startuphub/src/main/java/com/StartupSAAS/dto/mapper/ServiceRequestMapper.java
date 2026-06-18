package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.ServiceRequestResponseDTO;
import com.StartupSAAS.entity.Client;
import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.entity.ServiceRequest;

public class ServiceRequestMapper {

    public static ServiceRequestResponseDTO toDTO(ServiceRequest sr) {

        ServiceRequestResponseDTO dto = new ServiceRequestResponseDTO();
        dto.setId(sr.getId());
        dto.setTitle(sr.getTitle());
        dto.setDescription(sr.getDescription());
        dto.setStatus(sr.getStatus());
        dto.setPriority(sr.getPriority());
        dto.setDeadline(sr.getDeadline());
        dto.setAssignedAt(sr.getAssignedAt());
        dto.setCompletedAt(sr.getCompletedAt());
        dto.setAgreedPrice(sr.getAgreedPrice());
        dto.setCurrentStage(sr.getCurrentStage());
        dto.setSlaHours(sr.getSlaHours());
        dto.setCreatedAt(sr.getCreatedAt());

        // Flatten HubService
        if (sr.getHubService() != null) {
            dto.setHubServiceId(sr.getHubService().getId());
            dto.setHubServiceName(sr.getHubService().getName());
            dto.setHubServicePrice(sr.getHubService().getPrice());
        }

        // Flatten Client
        Client client = sr.getClient();
        if (client != null) {
            dto.setClientId(client.getId());
            if (client.getUser() != null) {
                dto.setClientFirstName(client.getUser().getFirstName());
                dto.setClientLastName(client.getUser().getLastName());
                dto.setClientEmail(client.getUser().getEmail());
            }
        }

        // Flatten Company
        if (sr.getCompany() != null) {
            dto.setCompanyId(sr.getCompany().getId());
            dto.setCompanyName(sr.getCompany().getName());
        }

        // Flatten Assigned Employee
        Employee emp = sr.getAssignedEmployee();
        if (emp != null) {
            dto.setAssignedEmployeeId(emp.getId());
            dto.setAssignedEmployeeDesignation(emp.getDesignation());
            if (emp.getUser() != null) {
                dto.setAssignedEmployeeFirstName(emp.getUser().getFirstName());
                dto.setAssignedEmployeeLastName(emp.getUser().getLastName());
            }
        }

        return dto;
    }
}
