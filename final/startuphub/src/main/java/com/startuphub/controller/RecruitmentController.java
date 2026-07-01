package com.startuphub.controller;

import com.startuphub.dto.request.JobApplicationRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.JobApplicationResponse;
import com.startuphub.enums.ApplicationStatus;
import com.startuphub.service.RecruitmentService;
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

@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
@Tag(name = "Recruitment", description = "Job applications and candidate pipeline")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    @PostMapping("/jobs/{jobPostingId}/apply")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Apply for a job posting")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> apply(
            @PathVariable Long jobPostingId,
            @Valid @RequestBody JobApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Application submitted",
                recruitmentService.apply(jobPostingId, request)));
    }

    @GetMapping("/applications")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "List all applications with optional status filter")
    public ResponseEntity<ApiResponse<Page<JobApplicationResponse>>> listAll(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            recruitmentService.listAll(status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/jobs/{jobPostingId}/applications")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "List all applications for a specific job posting")
    public ResponseEntity<ApiResponse<Page<JobApplicationResponse>>> listByPosting(
            @PathVariable Long jobPostingId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            recruitmentService.listByPosting(jobPostingId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/applications/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "Get application by ID")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(recruitmentService.getById(id)));
    }

    @PatchMapping("/applications/{id}/status")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "Update application status")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
            recruitmentService.updateStatus(id, status, notes)));
    }

    @DeleteMapping("/applications/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Delete application")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        recruitmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Application deleted"));
    }
}
