package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.TaskRequestDTO;
import com.StartupSAAS.dto.request.TaskStatusDTO;
import com.StartupSAAS.dto.response.TaskResponseDTO;
import com.StartupSAAS.enums.TaskStatus;

import java.util.List;

public interface TaskService {

    TaskResponseDTO create(TaskRequestDTO dto);
    List<TaskResponseDTO> getAll();
    TaskResponseDTO getById(Long id);
    List<TaskResponseDTO> getByServiceRequest(Long serviceRequestId);
    List<TaskResponseDTO> getByEmployee(Long employeeId);
    List<TaskResponseDTO> getByServiceRequestAndStatus(Long serviceRequestId, TaskStatus status);
    TaskResponseDTO updateStatus(Long id, TaskStatusDTO dto);
    TaskResponseDTO update(Long id, TaskRequestDTO dto);
    void delete(Long id);
}
