package com.startuphub.controller;

import com.startuphub.dto.request.CreateInvoiceRequest;
import com.startuphub.dto.request.RecordPaymentRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.InvoiceResponse;
import com.startuphub.dto.response.PaymentResponse;
import com.startuphub.enums.InvoiceStatus;
import com.startuphub.service.InvoiceService;
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
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Invoice creation, issuance, and payment recording")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Create a draft invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> create(
            @Valid @RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Invoice created", invoiceService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "List invoices with optional status filter")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> listAll(
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            invoiceService.listAll(status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "List invoices for a specific client")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> listForClient(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            invoiceService.listForClient(clientId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get invoice by ID with payment history")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getById(id)));
    }

    @PatchMapping("/{id}/issue")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Issue a draft invoice — sends notification to client")
    public ResponseEntity<ApiResponse<InvoiceResponse>> issue(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Invoice issued", invoiceService.issue(id)));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Cancel an invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Invoice cancelled", invoiceService.cancel(id)));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Record a payment against an invoice — credits company wallet")
    public ResponseEntity<ApiResponse<PaymentResponse>> recordPayment(
            @PathVariable Long id,
            @Valid @RequestBody RecordPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Payment recorded", invoiceService.recordPayment(id, request)));
    }

    @GetMapping("/{id}/payments")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get payment history for an invoice")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getPayments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            invoiceService.getPayments(id,
                PageRequest.of(page, size, Sort.by("paidAt").descending()))));
    }
}
