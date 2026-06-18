package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.TaskRequestDTO;
import com.StartupSAAS.dto.request.TaskStatusDTO;
import com.StartupSAAS.dto.response.TaskResponseDTO;
import com.StartupSAAS.enums.TaskStatus;
import com.StartupSAAS.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@RequestBody TaskRequestDTO dto) {
        return new ResponseEntity<>(taskService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public TaskResponseDTO getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @GetMapping("/service-request/{serviceRequestId}")
    public ResponseEntity<List<TaskResponseDTO>> getByServiceRequest(
            @PathVariable Long serviceRequestId) {
        List<TaskResponseDTO> list = taskService.getByServiceRequest(serviceRequestId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TaskResponseDTO>> getByEmployee(
            @PathVariable Long employeeId) {
        List<TaskResponseDTO> list = taskService.getByEmployee(employeeId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    // GET /api/tasks/service-request/3/status/IN_PROGRESS
    @GetMapping("/service-request/{serviceRequestId}/status/{status}")
    public ResponseEntity<List<TaskResponseDTO>> getByServiceRequestAndStatus(
            @PathVariable Long serviceRequestId, @PathVariable String status) {
        TaskStatus ts;
        try { ts = TaskStatus.valueOf(status.toUpperCase()); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().build(); }
        List<TaskResponseDTO> list =
                taskService.getByServiceRequestAndStatus(serviceRequestId, ts);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    // PATCH /api/tasks/1/status
    @PatchMapping("/{id}/status")
    public TaskResponseDTO updateStatus(
            @PathVariable Long id, @RequestBody TaskStatusDTO dto) {
        return taskService.updateStatus(id, dto);
    }

    @PutMapping("/{id}")
    public TaskResponseDTO update(
            @PathVariable Long id, @RequestBody TaskRequestDTO dto) {
        return taskService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
