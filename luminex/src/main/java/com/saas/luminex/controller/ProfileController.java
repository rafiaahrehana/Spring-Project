package com.saas.luminex.controller;

import com.saas.luminex.dto.request.PasswordChangeRequest;
import com.saas.luminex.dto.response.ApiResponse;
import com.saas.luminex.dto.response.UserResponse;
import com.saas.luminex.service.impl.PasswordService;
import com.saas.luminex.service.impl.UserServiceImpl;
import com.saas.luminex.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserServiceImpl userService;
    private final PasswordService passwordService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success(userService.getMyProfile()));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @RequestBody Map<String, Object> updates) {
        Long id = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Profile updated", userService.updateProfile(id, updates)));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request) {
        passwordService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}
