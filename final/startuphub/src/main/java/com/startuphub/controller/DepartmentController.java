package com.startuphub.controller;

import com.startuphub.dto.request.DepartmentRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.DepartmentResponse;
import com.startuphub.service.DepartmentService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Organisational unit management")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Create a department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> create(
            @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Department created", departmentService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "List all departments (paginated)")
    public ResponseEntity<ApiResponse<Page<DepartmentResponse>>> listAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            departmentService.listAll(PageRequest.of(page, size, Sort.by("name")))));
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List active departments — no pagination, used for dropdowns")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> listActive() {
        return ResponseEntity.ok(ApiResponse.success(departmentService.listActive()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Update department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Department updated",
            departmentService.update(id, request)));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Toggle department active/inactive")
    public ResponseEntity<ApiResponse<DepartmentResponse>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.toggleActive(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Delete department (only if no employees)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted"));
    }
}
