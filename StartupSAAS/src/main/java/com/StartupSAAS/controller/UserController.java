package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.PasswordChangeRequest;
import com.StartupSAAS.dto.request.UserRequest;
import com.StartupSAAS.dto.response.UserResponse;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping(value = "/me", consumes = {"multipart/form-data"})
    public ResponseEntity<UserResponse> updateMyProfile(
            @RequestPart("data") UserRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(userService.updateMyProfile(request, image));
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }

    // super admin only
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // super admin only
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    // super admin only
    @PutMapping("/{id}/toggle")
    public ResponseEntity<String> toggleActive(@PathVariable Long id) {
        userService.toggleUserActive(id);
        return ResponseEntity.ok("User status updated");
    }

    // super admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}