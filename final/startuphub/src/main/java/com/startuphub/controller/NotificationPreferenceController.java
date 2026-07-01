package com.startuphub.controller;

import com.startuphub.dto.request.UpdateNotificationPreferenceRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.NotificationPreferenceResponse;
import com.startuphub.service.NotificationPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Notification preference endpoints — authenticated users only.
 *
 * Every authenticated user can manage their own preferences.
 * No cross-user access — preferences are always for the calling user.
 *
 * GET    /api/notification-preferences        — get own preferences
 * PUT    /api/notification-preferences        — replace own preferences
 * DELETE /api/notification-preferences        — reset to defaults
 */
@RestController
@RequestMapping("/api/notification-preferences")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Notification Preferences",
     description = "User notification settings — email and in-app preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    @GetMapping
    @Operation(summary = "Get notification preferences for the authenticated user")
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> get() {
        return ResponseEntity.ok(ApiResponse.success(
            preferenceService.getForCurrentUser()));
    }

    @PutMapping
    @Operation(summary = "Update notification preferences — full replacement")
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> update(
            @Valid @RequestBody UpdateNotificationPreferenceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            "Notification preferences saved",
            preferenceService.update(request)));
    }

    @DeleteMapping
    @Operation(summary = "Reset notification preferences to platform defaults")
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> reset() {
        return ResponseEntity.ok(ApiResponse.success(
            "Notification preferences reset to defaults",
            preferenceService.resetToDefaults()));
    }
}
