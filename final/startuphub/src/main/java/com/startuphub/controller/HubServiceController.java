package com.startuphub.controller;

import com.startuphub.dto.request.HubServiceRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.HubServiceResponse;
import com.startuphub.service.HubServiceService;
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
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Hub Services", description = "Company service catalog management")
public class HubServiceController {

    private final HubServiceService hubServiceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Create a service offering")
    public ResponseEntity<ApiResponse<HubServiceResponse>> create(
            @Valid @RequestBody HubServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Service created", hubServiceService.create(request)));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all services (paginated), optionally filter by category")
    public ResponseEntity<ApiResponse<Page<HubServiceResponse>>> listAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            hubServiceService.listAll(categoryId,
                PageRequest.of(page, size, Sort.by("name")))));
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List active services — for client service request form")
    public ResponseEntity<ApiResponse<List<HubServiceResponse>>> listActive() {
        return ResponseEntity.ok(ApiResponse.success(hubServiceService.listActive()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get service by ID")
    public ResponseEntity<ApiResponse<HubServiceResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(hubServiceService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Update service")
    public ResponseEntity<ApiResponse<HubServiceResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody HubServiceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Service updated",
            hubServiceService.update(id, request)));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Toggle service active/inactive")
    public ResponseEntity<ApiResponse<HubServiceResponse>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(hubServiceService.toggleActive(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Soft-delete service")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        hubServiceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Service deleted"));
    }
}
