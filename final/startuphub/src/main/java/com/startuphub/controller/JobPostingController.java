package com.startuphub.controller;

import com.startuphub.dto.request.JobPostingRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.JobPostingResponse;
import com.startuphub.enums.JobPostingStatus;
import com.startuphub.service.JobPostingService;
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
@RequestMapping("/api/recruitment/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Postings", description = "Recruitment job posting management")
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Create a job posting (starts as DRAFT)")
    public ResponseEntity<ApiResponse<JobPostingResponse>> create(
            @Valid @RequestBody JobPostingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Job posting created",
                jobPostingService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "List job postings with optional status filter")
    public ResponseEntity<ApiResponse<Page<JobPostingResponse>>> listAll(
            @RequestParam(required = false) JobPostingStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            jobPostingService.listAll(status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Get job posting by ID")
    public ResponseEntity<ApiResponse<JobPostingResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(jobPostingService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Update job posting (only DRAFT or OPEN, not CLOSED)")
    public ResponseEntity<ApiResponse<JobPostingResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody JobPostingRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Job posting updated",
            jobPostingService.update(id, request)));
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Publish job posting — sets status to OPEN")
    public ResponseEntity<ApiResponse<JobPostingResponse>> publish(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Job posting published",
            jobPostingService.publish(id)));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Close job posting")
    public ResponseEntity<ApiResponse<JobPostingResponse>> close(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Job posting closed",
            jobPostingService.close(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Delete job posting (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        jobPostingService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Job posting deleted"));
    }
}
