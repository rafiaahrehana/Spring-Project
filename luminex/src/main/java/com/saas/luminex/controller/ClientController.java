package com.saas.luminex.controller;

import com.saas.luminex.dto.request.PaymentRequest;
import com.saas.luminex.dto.request.ServiceRequestCreateRequest;
import com.saas.luminex.dto.response.ApiResponse;
import com.saas.luminex.dto.response.DashboardStatsResponse;
import com.saas.luminex.dto.response.ServiceRequestResponse;
import com.saas.luminex.entity.Payment;
import com.saas.luminex.service.DashboardService;
import com.saas.luminex.service.PaymentService;
import com.saas.luminex.service.ServiceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
@PreAuthorize("hasRole('CLIENT')")
@RequiredArgsConstructor
public class ClientController {

    private final ServiceRequestService requestService;
    private final PaymentService paymentService;
    private final DashboardService dashboardService;

    // Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getClientStats()));
    }

    // My Requests
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<Page<ServiceRequestResponse>>> getMyRequests(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(requestService.getMyRequests(pageable)));
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<ApiResponse<ServiceRequestResponse>> getRequest(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(requestService.getRequestById(id)));
    }

    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<ServiceRequestResponse>> createRequest(
            @Valid @RequestBody ServiceRequestCreateRequest dto) {
        return ResponseEntity.ok(ApiResponse.success("Request submitted", requestService.createRequest(dto)));
    }

    // Payments
    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<Page<Payment>>> getMyPayments(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getMyPayments(pageable)));
    }

    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<Payment>> makePayment(
            @Valid @RequestBody PaymentRequest dto) {
        return ResponseEntity.ok(ApiResponse.success("Payment submitted", paymentService.createPayment(dto)));
    }
}
