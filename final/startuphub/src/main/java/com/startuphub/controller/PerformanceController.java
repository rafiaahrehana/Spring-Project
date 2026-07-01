package com.startuphub.controller;

import com.startuphub.dto.request.PerformanceReviewRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.PerformanceReviewResponse;
import com.startuphub.service.PerformanceService;
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
@RequestMapping("/api/hr/performance")
@RequiredArgsConstructor
@Tag(name = "Performance Reviews", description = "Employee performance review management")
public class PerformanceController {

    private final PerformanceService performanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Create a performance review")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> create(
            @Valid @RequestBody PerformanceReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Review created", performanceService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "List all performance reviews")
    public ResponseEntity<ApiResponse<Page<PerformanceReviewResponse>>> listAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            performanceService.listAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "List performance reviews for an employee")
    public ResponseEntity<ApiResponse<Page<PerformanceReviewResponse>>> listForEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            performanceService.listForEmployee(employeeId,
                PageRequest.of(page, size, Sort.by("reviewPeriodStart").descending()))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(performanceService.getById(id)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Update a performance review (draft only)")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Review updated",
            performanceService.update(id, request)));
    }

    @PatchMapping("/{id}/finalise")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Finalise a performance review — locks it from further edits")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> finalise(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Review finalised",
            performanceService.finalise(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Delete a non-finalised review")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        performanceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted"));
    }
}
