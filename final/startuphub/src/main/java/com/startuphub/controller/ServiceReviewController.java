package com.startuphub.controller;

import com.startuphub.dto.request.ServiceReviewRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.ServiceReviewResponse;
import com.startuphub.service.ServiceReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Service Reviews", description = "Client ratings for completed service requests")
public class ServiceReviewController {

    private final ServiceReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Submit a review for a completed service request — CLIENT only")
    public ResponseEntity<ApiResponse<ServiceReviewResponse>> submit(
            @Valid @RequestBody ServiceReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Review submitted", reviewService.submit(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "List all service reviews")
    public ResponseEntity<ApiResponse<Page<ServiceReviewResponse>>> listAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            reviewService.listAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/service/{hubServiceId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List reviews for a specific service")
    public ResponseEntity<ApiResponse<Page<ServiceReviewResponse>>> listByService(
            @PathVariable Long hubServiceId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            reviewService.listByService(hubServiceId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ApiResponse<ServiceReviewResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Delete a review")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted"));
    }
}
