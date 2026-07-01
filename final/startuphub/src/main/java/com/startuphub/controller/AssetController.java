package com.startuphub.controller;

import com.startuphub.dto.request.AssetRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.AssetResponse;
import com.startuphub.enums.AssetStatus;
import com.startuphub.service.AssetService;
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
@RequestMapping("/api/hr/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Company asset inventory and employee assignment")
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Add a new asset to inventory")
    public ResponseEntity<ApiResponse<AssetResponse>> create(
            @Valid @RequestBody AssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Asset added", assetService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "List all assets with optional status filter")
    public ResponseEntity<ApiResponse<Page<AssetResponse>>> listAll(
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            assetService.listAll(status,
                PageRequest.of(page, size, Sort.by("name")))));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "List assets assigned to an employee")
    public ResponseEntity<ApiResponse<List<AssetResponse>>> listForEmployee(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success(assetService.listForEmployee(employeeId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Get asset by ID")
    public ResponseEntity<ApiResponse<AssetResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(assetService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Update asset details")
    public ResponseEntity<ApiResponse<AssetResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Asset updated", assetService.update(id, request)));
    }

    @PatchMapping("/{id}/assign/{employeeId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Assign asset to an employee")
    public ResponseEntity<ApiResponse<AssetResponse>> assign(
            @PathVariable Long id,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Asset assigned",
            assetService.assign(id, employeeId)));
    }

    @PatchMapping("/{id}/unassign")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Return asset from employee — marks as AVAILABLE")
    public ResponseEntity<ApiResponse<AssetResponse>> unassign(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Asset returned", assetService.unassign(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Delete asset (only AVAILABLE assets)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        assetService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Asset deleted"));
    }
}
