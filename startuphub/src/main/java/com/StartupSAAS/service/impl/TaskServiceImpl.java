package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.TaskMapper;
import com.StartupSAAS.dto.request.TaskRequestDTO;
import com.StartupSAAS.dto.request.TaskStatusDTO;
import com.StartupSAAS.dto.response.TaskResponseDTO;
import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.entity.ServiceRequest;
import com.StartupSAAS.entity.Task;
import com.StartupSAAS.enums.TaskStatus;
import com.StartupSAAS.repository.EmployeeRepository;
import com.StartupSAAS.repository.ServiceRequestRepository;
import com.StartupSAAS.repository.TaskRepository;
import com.StartupSAAS.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    @Override
    public TaskResponseDTO create(TaskRequestDTO dto) {

        ServiceRequest sr = serviceRequestRepository.findById(dto.getServiceRequestId())
                .orElseThrow(() -> new RuntimeException(
                        "ServiceRequest not found with id: " + dto.getServiceRequestId()));

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        task.setNotes(dto.getNotes());
        task.setStatus(TaskStatus.TODO);
        task.setServiceRequest(sr);

        if (dto.getAssignedEmployeeId() != null) {
            Employee emp = employeeRepository.findById(dto.getAssignedEmployeeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found with id: " + dto.getAssignedEmployeeId()));
            task.setAssignedEmployee(emp);
        }

        if (dto.getCreatedById() != null) {
            Employee creator = employeeRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found with id: " + dto.getCreatedById()));
            task.setCreatedBy(creator);
        }

        Task saved = taskRepository.save(task);
        return TaskMapper.toDTO(
                taskRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAll() {
        return taskRepository.findAll()
                .stream().map(TaskMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getById(Long id) {
        return TaskMapper.toDTO(
                taskRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Task not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getByServiceRequest(Long serviceRequestId) {
        return taskRepository.findByServiceRequestId(serviceRequestId)
                .stream().map(TaskMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getByEmployee(Long employeeId) {
        return taskRepository.findByAssignedEmployeeId(employeeId)
                .stream().map(TaskMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getByServiceRequestAndStatus(
            Long serviceRequestId, TaskStatus status) {
        return taskRepository.findByServiceRequestIdAndStatus(serviceRequestId, status)
                .stream().map(TaskMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public TaskResponseDTO updateStatus(Long id, TaskStatusDTO dto) {

        Task task = taskRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "Task not found with id: " + id));

        TaskStatus newStatus;
        try {
            newStatus = TaskStatus.valueOf(dto.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + dto.getStatus());
        }

        task.setStatus(newStatus);
        if (dto.getNotes() != null) task.setNotes(dto.getNotes());

        // Auto-set completedAt when task is done
        if (newStatus == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        }

        Task saved = taskRepository.save(task);
        return TaskMapper.toDTO(
                taskRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Transactional
    @Override
    public TaskResponseDTO update(Long id, TaskRequestDTO dto) {

        Task task = taskRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "Task not found with id: " + id));

        if (dto.getTitle() != null)       task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getPriority() != null)    task.setPriority(dto.getPriority());
        if (dto.getDueDate() != null)     task.setDueDate(dto.getDueDate());
        if (dto.getNotes() != null)       task.setNotes(dto.getNotes());

        if (dto.getAssignedEmployeeId() != null) {
            Employee emp = employeeRepository.findById(dto.getAssignedEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            task.setAssignedEmployee(emp);
        }

        Task saved = taskRepository.save(task);
        return TaskMapper.toDTO(
                taskRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
