package com.saas.luminex.controller;

import com.saas.luminex.dto.request.UserCreateRequest;
import com.saas.luminex.dto.response.ApiResponse;
import com.saas.luminex.dto.response.UserResponse;
import com.saas.luminex.enums.Role;
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
@RequestMapping("/api/superadmin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class SuperAdminController {

    private final UserService userService;
    private final AuditLogService auditLogService;

    // ─── Admin Management ─────────────────────────────────────────────────────

    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAdmins(
            @PageableDefault(size = 15) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUsersByRole(Role.ADMIN, pageable)));
    }

    @GetMapping("/admins/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PostMapping("/admins")
    public ResponseEntity<ApiResponse<UserResponse>> createAdmin(
            @Valid @RequestBody UserCreateRequest dto) {
        UserResponse admin = userService.createUser(
                dto.getName(), dto.getEmail(), dto.getPassword(),
                Role.ADMIN, dto.getCompanyName(), dto.getAddress(), dto.getPhone());
        auditLogService.log("CREATE_ADMIN", "SuperAdmin created admin: " + admin.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Admin created", admin));
    }

    @PatchMapping("/admins/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateAdmin(
            @PathVariable Long id, @RequestBody Map<String, Object> updates) {
        UserResponse updated = userService.updateProfile(id, updates);
        auditLogService.log("UPDATE_ADMIN", "Updated admin ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Admin updated", updated));
    }

    @PatchMapping("/admins/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleAdminActive(@PathVariable Long id) {
        userService.toggleUserActive(id);
        auditLogService.log("TOGGLE_ADMIN_ACTIVE", "Toggled admin ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Admin status updated", null));
    }

    @DeleteMapping("/admins/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAdmin(@PathVariable Long id) {
        userService.deleteUser(id);
        auditLogService.log("DELETE_ADMIN", "SuperAdmin deleted admin ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Admin deleted", null));
    }

    // ─── Platform-wide user management ───────────────────────────────────────

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getAnyUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAnyUser(@PathVariable Long id) {
        userService.deleteUser(id);
        auditLogService.log("DELETE_USER", "SuperAdmin deleted user ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }
}
