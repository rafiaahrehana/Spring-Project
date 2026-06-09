package com.saas.luminex.controller;

import com.saas.luminex.dto.request.ServiceRequestUpdateRequest;
import com.saas.luminex.dto.response.ApiResponse;
import com.saas.luminex.dto.response.DashboardStatsResponse;
import com.saas.luminex.dto.response.ServiceRequestResponse;
import com.saas.luminex.entity.KnowledgeBase;
import com.saas.luminex.exception.ResourceNotFoundException;
import com.saas.luminex.repository.KnowledgeBaseRepository;
import com.saas.luminex.service.DashboardService;
import com.saas.luminex.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class EmployeeController {

    private final ServiceRequestService requestService;
    private final DashboardService dashboardService;
    private final KnowledgeBaseRepository knowledgeBaseRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getEmployeeStats()));
    }

    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<Page<ServiceRequestResponse>>> getMyTasks(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(requestService.getMyAssignedTasks(pageable)));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse<ServiceRequestResponse>> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(requestService.getRequestById(id)));
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse<ServiceRequestResponse>> updateTask(
            @PathVariable Long id,
            @RequestBody ServiceRequestUpdateRequest dto) {
        // Employees can only update progress, workedHours and status — not reassign
        dto.setAssignedEmployeeId(null);
        dto.setAdminNotes(null);
        return ResponseEntity.ok(ApiResponse.success("Task updated", requestService.updateRequest(id, dto)));
    }

    @GetMapping("/knowledge-base")
    public ResponseEntity<ApiResponse<Page<KnowledgeBase>>> getKnowledgeBase(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(knowledgeBaseRepository.findAll(pageable)));
    }

    @GetMapping("/knowledge-base/{id}")
    public ResponseEntity<ApiResponse<KnowledgeBase>> getArticle(@PathVariable Long id) {
        KnowledgeBase article = knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KnowledgeBase", id));
        return ResponseEntity.ok(ApiResponse.success(article));
    }
}
