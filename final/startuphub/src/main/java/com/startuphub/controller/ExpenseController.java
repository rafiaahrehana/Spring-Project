package com.startuphub.controller;

import com.startuphub.dto.request.ExpenseRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.ExpenseResponse;
import com.startuphub.enums.ExpenseStatus;
import com.startuphub.service.ExpenseService;
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
@RequestMapping("/api/hr/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Employee expense claims and reimbursements")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Submit an expense claim")
    public ResponseEntity<ApiResponse<ExpenseResponse>> submit(
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Expense submitted", expenseService.submit(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "List all expense claims with optional status filter")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> listAll(
            @RequestParam(required = false) ExpenseStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            expenseService.listAll(status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "List own expense claims")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> listMine(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            expenseService.listMine(
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get expense by ID")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(expenseService.getById(id)));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Approve an expense claim")
    public ResponseEntity<ApiResponse<ExpenseResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Expense approved", expenseService.approve(id)));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Reject an expense claim")
    public ResponseEntity<ApiResponse<ExpenseResponse>> reject(
            @PathVariable Long id,
            @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success("Expense rejected",
            expenseService.reject(id, reason)));
    }

    @PatchMapping("/{id}/reimburse")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Mark an approved expense as reimbursed")
    public ResponseEntity<ApiResponse<ExpenseResponse>> reimburse(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Expense reimbursed",
            expenseService.markReimbursed(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Delete a PENDING expense claim")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted"));
    }
}
