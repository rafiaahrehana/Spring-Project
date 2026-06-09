package com.saas.luminex.controller;

import com.saas.luminex.dto.request.ServiceRequestUpdateRequest;
import com.saas.luminex.dto.request.UserCreateRequest;
import com.saas.luminex.dto.response.ApiResponse;
import com.saas.luminex.dto.response.DashboardStatsResponse;
import com.saas.luminex.dto.response.ServiceRequestResponse;
import com.saas.luminex.dto.response.UserResponse;
import com.saas.luminex.entity.AuditLog;
import com.saas.luminex.entity.Payment;
import com.saas.luminex.enums.PaymentStatus;
import com.saas.luminex.enums.Role;
import com.saas.luminex.service.DashboardService;
import com.saas.luminex.service.PaymentService;
import com.saas.luminex.service.ServiceRequestService;
import com.saas.luminex.service.UserService;
import com.saas.luminex.service.impl.AuditLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ServiceRequestService requestService;
    private final PaymentService paymentService;
    private final DashboardService dashboardService;
    private final AuditLogService auditLogService;

    // ─── Dashboard ────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getAdminStats()));
    }

    // ─── User Management ──────────────────────────────────────────────────────

    @GetMapping("/clients")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getClients(
            @PageableDefault(size = 15) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUsersByRole(Role.CLIENT, pageable)));
    }

    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getEmployees(
            @PageableDefault(size = 15) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUsersByRole(Role.EMPLOYEE, pageable)));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PostMapping("/employees")
    public ResponseEntity<ApiResponse<UserResponse>> createEmployee(
            @Valid @RequestBody UserCreateRequest dto) {
        UserResponse user = userService.createUser(
                dto.getName(), dto.getEmail(), dto.getPassword(),
                Role.EMPLOYEE, dto.getCompanyName(), dto.getAddress(), dto.getPhone());
        auditLogService.log("CREATE_EMPLOYEE", "Created employee: " + user.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Employee created", user));
    }

    @PostMapping("/clients")
    public ResponseEntity<ApiResponse<UserResponse>> createClient(
            @Valid @RequestBody UserCreateRequest dto) {
        UserResponse user = userService.createUser(
                dto.getName(), dto.getEmail(), dto.getPassword(),
                Role.CLIENT, dto.getCompanyName(), dto.getAddress(), dto.getPhone());
        auditLogService.log("CREATE_CLIENT", "Admin created client: " + user.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Client created", user));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id, @RequestBody Map<String, Object> updates) {
        UserResponse updated = userService.updateProfile(id, updates);
        auditLogService.log("UPDATE_USER", "Updated user ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("User updated", updated));
    }

    @PatchMapping("/users/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable Long id) {
        userService.toggleUserActive(id);
        auditLogService.log("TOGGLE_USER_ACTIVE", "Toggled active status for user ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("User status updated", null));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        auditLogService.log("DELETE_USER", "Deleted user ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }

    // ─── Service Requests ─────────────────────────────────────────────────────

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<Page<ServiceRequestResponse>>> getAllRequests(
            @PageableDefault(size = 15, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(requestService.getAllRequests(pageable)));
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<ApiResponse<ServiceRequestResponse>> getRequest(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(requestService.getRequestById(id)));
    }

    @PatchMapping("/requests/{id}")
    public ResponseEntity<ApiResponse<ServiceRequestResponse>> updateRequest(
            @PathVariable Long id, @RequestBody ServiceRequestUpdateRequest dto) {
        ServiceRequestResponse updated = requestService.updateRequest(id, dto);
        auditLogService.log("UPDATE_REQUEST",
                "Updated request ID: " + id + " → status: " + dto.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Request updated", updated));
    }

    @DeleteMapping("/requests/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
        auditLogService.log("DELETE_REQUEST", "Deleted request ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Request deleted", null));
    }

    // ─── Payments ─────────────────────────────────────────────────────────────

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<Page<Payment>>> getAllPayments(
            @PageableDefault(size = 15) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments(pageable)));
    }

    @GetMapping("/payments/revenue")
    public ResponseEntity<ApiResponse<java.math.BigDecimal>> getTotalRevenue() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getTotalRevenue()));
    }

    @PatchMapping("/payments/{id}/status")
    public ResponseEntity<ApiResponse<Payment>> updatePaymentStatus(
            @PathVariable Long id, @RequestParam PaymentStatus status) {
        Payment updated = paymentService.updatePaymentStatus(id, status);
        auditLogService.log("UPDATE_PAYMENT", "Payment ID " + id + " → " + status);
        return ResponseEntity.ok(ApiResponse.success("Payment status updated", updated));
    }

    // ─── Audit Logs ───────────────────────────────────────────────────────────

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogs(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(auditLogService.getAllLogs(pageable)));
    }
}
