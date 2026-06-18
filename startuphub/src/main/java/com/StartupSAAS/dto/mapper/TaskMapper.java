package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.TaskResponseDTO;
import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.entity.Task;

public class TaskMapper {

    public static TaskResponseDTO toDTO(Task task) {

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setNotes(task.getNotes());
        dto.setCreatedAt(task.getCreatedAt());

        // Flatten ServiceRequest
        if (task.getServiceRequest() != null) {
            dto.setServiceRequestId(task.getServiceRequest().getId());
            dto.setServiceRequestTitle(task.getServiceRequest().getTitle());
            dto.setServiceRequestStatus(task.getServiceRequest().getStatus() != null
                    ? task.getServiceRequest().getStatus().name() : null);
        }

        // Flatten Assigned Employee
        Employee assigned = task.getAssignedEmployee();
        if (assigned != null) {
            dto.setAssignedEmployeeId(assigned.getId());
            dto.setAssignedEmployeeDesignation(assigned.getDesignation());
            if (assigned.getUser() != null) {
                dto.setAssignedEmployeeFirstName(assigned.getUser().getFirstName());
                dto.setAssignedEmployeeLastName(assigned.getUser().getLastName());
            }
        }

        // Flatten Created By
        Employee createdBy = task.getCreatedBy();
        if (createdBy != null) {
            dto.setCreatedById(createdBy.getId());
            if (createdBy.getUser() != null) {
                dto.setCreatedByFirstName(createdBy.getUser().getFirstName());
                dto.setCreatedByLastName(createdBy.getUser().getLastName());
            }
        }

        return dto;
    }
}
