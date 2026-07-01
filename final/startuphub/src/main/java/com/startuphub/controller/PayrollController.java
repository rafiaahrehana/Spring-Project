package com.startuphub.controller;

import com.startuphub.dto.request.CreatePayrollRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.PayrollResponse;
import com.startuphub.service.PayrollService;
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
@RequestMapping("/api/hr/payroll")
@RequiredArgsConstructor
@Tag(name = "Payroll", description = "Monthly payroll creation, approval and payment")
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Create a payroll record for an employee")
    public ResponseEntity<ApiResponse<PayrollResponse>> create(
            @Valid @RequestBody CreatePayrollRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Payroll created", payrollService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "List payrolls for a given month and year")
    public ResponseEntity<ApiResponse<Page<PayrollResponse>>> listByPeriod(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            payrollService.listByPeriod(month, year,
                PageRequest.of(page, size, Sort.by("createdAt")))));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "List payroll history for an employee")
    public ResponseEntity<ApiResponse<Page<PayrollResponse>>> listForEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "24") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            payrollService.listForEmployee(employeeId,
                PageRequest.of(page, size, Sort.by("payYear").descending()
                    .and(Sort.by("payMonth").descending())))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Get payroll record by ID")
    public ResponseEntity<ApiResponse<PayrollResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.getById(id)));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Approve a DRAFT payroll")
    public ResponseEntity<ApiResponse<PayrollResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Payroll approved", payrollService.approve(id)));
    }

    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Mark an APPROVED payroll as paid")
    public ResponseEntity<ApiResponse<PayrollResponse>> markPaid(
            @PathVariable Long id,
            @RequestParam(required = false) String paymentReference) {
        return ResponseEntity.ok(ApiResponse.success("Payroll marked as paid",
            payrollService.markPaid(id, paymentReference)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Delete a DRAFT or CANCELLED payroll")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        payrollService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Payroll deleted"));
    }
}
