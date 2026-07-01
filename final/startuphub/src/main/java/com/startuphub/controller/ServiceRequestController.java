package com.startuphub.controller;

import com.startuphub.dto.request.AddCommentRequest;
import com.startuphub.dto.request.ChangeRequestStatusRequest;
import com.startuphub.dto.request.CreateServiceRequestRequest;
import com.startuphub.dto.request.CreateTaskRequest;
import com.startuphub.dto.request.UpdateServiceRequestRequest;
import com.startuphub.dto.request.UpdateTaskRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.RequestCommentResponse;
import com.startuphub.dto.response.RequestStatusHistoryResponse;
import com.startuphub.dto.response.ServiceRequestResponse;
import com.startuphub.dto.response.TaskResponse;
import com.startuphub.enums.ServiceRequestStatus;
import com.startuphub.service.ServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
@RequiredArgsConstructor
@Tag(name = "Service Requests", description = "Core service request lifecycle management")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Submit a new service request — CLIENT only")
    public ResponseEntity<ApiResponse<ServiceRequestResponse>> create(
            @Valid @RequestBody CreateServiceRequestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Service request submitted",
                serviceRequestService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "List all service requests — staff view")
    public ResponseEntity<ApiResponse<Page<ServiceRequestResponse>>> listAll(
            @RequestParam(required = false) ServiceRequestStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            serviceRequestService.listAll(status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "List own requests — CLIENT only")
    public ResponseEntity<ApiResponse<Page<ServiceRequestResponse>>> listMyRequests(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            serviceRequestService.listMyRequests(
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/assigned-to-me")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "List requests assigned to the current employee")
    public ResponseEntity<ApiResponse<Page<ServiceRequestResponse>>> listAssignedToMe(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            serviceRequestService.listAssignedToMe(
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get service request by ID")
    public ResponseEntity<ApiResponse<ServiceRequestResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(serviceRequestService.getById(id)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Update service request details")
    public ResponseEntity<ApiResponse<ServiceRequestResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceRequestRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Request updated",
            serviceRequestService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Change request status")
    public ResponseEntity<ApiResponse<ServiceRequestResponse>> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeRequestStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
            serviceRequestService.changeStatus(id, request)));
    }

    @PatchMapping("/{id}/assign/{employeeId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Assign request to an employee")
    public ResponseEntity<ApiResponse<ServiceRequestResponse>> assign(
            @PathVariable Long id,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Request assigned",
            serviceRequestService.assign(id, employeeId)));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel a service request")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
        serviceRequestService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("Request cancelled"));
    }

    // ── Tasks ─────────────────────────────────────────────────────

    @PostMapping("/{id}/tasks")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Add a task to a service request")
    public ResponseEntity<ApiResponse<TaskResponse>> addTask(
            @PathVariable Long id,
            @Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Task added",
                serviceRequestService.addTask(id, request)));
    }

    @GetMapping("/{id}/tasks")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all tasks for a service request")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(serviceRequestService.getTasks(id)));
    }

    @PatchMapping("/{id}/tasks/{taskId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Update a task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Task updated",
            serviceRequestService.updateTask(id, taskId, request)));
    }

    @DeleteMapping("/{id}/tasks/{taskId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Soft-delete a task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long id,
            @PathVariable Long taskId) {
        serviceRequestService.deleteTask(id, taskId);
        return ResponseEntity.ok(ApiResponse.success("Task deleted"));
    }

    // ── Comments ──────────────────────────────────────────────────

    @PostMapping("/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add a comment to a service request")
    public ResponseEntity<ApiResponse<RequestCommentResponse>> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Comment added",
                serviceRequestService.addComment(id, request)));
    }

    @GetMapping("/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get comments for a service request")
    public ResponseEntity<ApiResponse<Page<RequestCommentResponse>>> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            serviceRequestService.getComments(id,
                PageRequest.of(page, size))));
    }

    // ── Status history ────────────────────────────────────────────

    @GetMapping("/{id}/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get full status history for a service request")
    public ResponseEntity<ApiResponse<List<RequestStatusHistoryResponse>>> getHistory(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
            serviceRequestService.getStatusHistory(id)));
    }
}
