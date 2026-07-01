package com.startuphub.controller;

import com.startuphub.dto.request.LeaveRequestDto;
import com.startuphub.dto.request.ReviewLeaveRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.LeaveBalanceResponse;
import com.startuphub.dto.response.LeaveRequestResponse;
import com.startuphub.enums.LeaveRequestStatus;
import com.startuphub.service.LeaveService;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hr/leaves")
@RequiredArgsConstructor
@Tag(name = "Leave Management", description = "Employee leave applications and approvals")
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Apply for leave")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> apply(
            @Valid @RequestBody LeaveRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Leave request submitted", leaveService.apply(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "List all leave requests — ADMIN view with optional status filter")
    public ResponseEntity<ApiResponse<Page<LeaveRequestResponse>>> listAll(
            @RequestParam(required = false) LeaveRequestStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            leaveService.listAll(status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "List own leave requests")
    public ResponseEntity<ApiResponse<Page<LeaveRequestResponse>>> listMine(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            leaveService.listMyLeaves(
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get leave request by ID")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getById(id)));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Approve or reject a leave request")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> review(
            @PathVariable Long id,
            @Valid @RequestBody ReviewLeaveRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave request reviewed",
            leaveService.review(id, request)));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Cancel own leave request")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
        leaveService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("Leave request cancelled"));
    }

    @GetMapping("/balances/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Get own leave balances for a given year")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getMyBalances(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getMyBalances(year)));
    }

    @GetMapping("/balances/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Get leave balances for a specific employee")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getBalancesForEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year) {
        return ResponseEntity.ok(ApiResponse.success(
            leaveService.getBalancesForEmployee(employeeId, year)));
    }
}
