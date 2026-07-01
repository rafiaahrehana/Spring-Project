package com.startuphub.controller;

import com.startuphub.dto.request.WorkflowStageRequest;
import com.startuphub.dto.request.WorkflowTemplateRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.WorkflowStageResponse;
import com.startuphub.dto.response.WorkflowTemplateResponse;
import com.startuphub.service.WorkflowService;
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
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
@Tag(name = "Workflows", description = "Workflow template and stage management")
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Create a workflow template")
    public ResponseEntity<ApiResponse<WorkflowTemplateResponse>> create(
            @Valid @RequestBody WorkflowTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Workflow template created",
                workflowService.createTemplate(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "List workflow templates (paginated)")
    public ResponseEntity<ApiResponse<Page<WorkflowTemplateResponse>>> listAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            workflowService.listTemplates(
                PageRequest.of(page, size, Sort.by("name")))));
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List active workflow templates — for service assignment dropdowns")
    public ResponseEntity<ApiResponse<List<WorkflowTemplateResponse>>> listActive() {
        return ResponseEntity.ok(ApiResponse.success(workflowService.listActiveTemplates()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Get workflow template by ID with all stages")
    public ResponseEntity<ApiResponse<WorkflowTemplateResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.getTemplateById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Update workflow template name/description (bumps version)")
    public ResponseEntity<ApiResponse<WorkflowTemplateResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody WorkflowTemplateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Workflow template updated",
            workflowService.updateTemplate(id, request)));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Toggle workflow template active/inactive")
    public ResponseEntity<ApiResponse<WorkflowTemplateResponse>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.toggleTemplate(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Soft-delete workflow template")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        workflowService.deleteTemplate(id);
        return ResponseEntity.ok(ApiResponse.success("Workflow template deleted"));
    }

    // ── Stages ────────────────────────────────────────────────────

    @PostMapping("/{templateId}/stages")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Add a stage to a workflow template")
    public ResponseEntity<ApiResponse<WorkflowStageResponse>> addStage(
            @PathVariable Long templateId,
            @Valid @RequestBody WorkflowStageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Stage added",
                workflowService.addStage(templateId, request)));
    }

    @PutMapping("/{templateId}/stages/{stageId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Update a workflow stage")
    public ResponseEntity<ApiResponse<WorkflowStageResponse>> updateStage(
            @PathVariable Long templateId,
            @PathVariable Long stageId,
            @Valid @RequestBody WorkflowStageRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stage updated",
            workflowService.updateStage(templateId, stageId, request)));
    }

    @DeleteMapping("/{templateId}/stages/{stageId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Remove a stage from a workflow template")
    public ResponseEntity<ApiResponse<Void>> deleteStage(
            @PathVariable Long templateId,
            @PathVariable Long stageId) {
        workflowService.deleteStage(templateId, stageId);
        return ResponseEntity.ok(ApiResponse.success("Stage removed"));
    }
}
