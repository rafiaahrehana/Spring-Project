package com.startuphub.controller;

import com.startuphub.dto.response.ApiResponse;
import com.startuphub.entity.FeatureFlag;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.repository.FeatureFlagRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Feature flag management — SUPER_ADMIN only.
 *
 * Default flags should be seeded via POST /seed on first deployment.
 * After seeding, use PATCH /{key}/toggle to enable or disable.
 */
@RestController
@RequestMapping("/api/feature-flags")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Feature Flags", description = "Platform-wide feature toggles — SUPER_ADMIN only")
public class FeatureFlagController {

    private static final Map<String, String> DEFAULT_FLAGS = Map.of(
        "ENABLE_BKASH",        "Show bKash payment option to clients",
        "ENABLE_NAGAD",        "Show Nagad payment option to clients",
        "ENABLE_REFERRAL",     "Enable the company referral programme",
        "ENABLE_AUTO_ASSIGN",  "Auto-assign service requests to staff by workload",
        "ENABLE_PACKAGES",     "Show bundled service packages in catalog",
        "MAINTENANCE_MODE",    "Block all requests — show maintenance page to users"
    );

    private final FeatureFlagRepository flagRepository;

    @GetMapping
    @Operation(summary = "Get all feature flags")
    public ResponseEntity<ApiResponse<List<FeatureFlag>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(flagRepository.findAll()));
    }

    @GetMapping("/{key}")
    @Operation(summary = "Get a specific flag by key")
    public ResponseEntity<ApiResponse<FeatureFlag>> getByKey(@PathVariable String key) {
        FeatureFlag flag = flagRepository.findByFlagKey(key)
            .orElseThrow(() -> new ResourceNotFoundException("Feature flag not found: " + key));
        return ResponseEntity.ok(ApiResponse.success(flag));
    }

    @PatchMapping("/{key}/toggle")
    @Transactional
    @Operation(summary = "Toggle a feature flag on or off")
    public ResponseEntity<ApiResponse<FeatureFlag>> toggle(@PathVariable String key) {
        FeatureFlag flag = flagRepository.findByFlagKey(key)
            .orElseThrow(() -> new ResourceNotFoundException("Feature flag not found: " + key));
        flag.setEnabled(!flag.isEnabled());
        flagRepository.save(flag);
        return ResponseEntity.ok(ApiResponse.success(
            key + " is now " + (flag.isEnabled() ? "ENABLED" : "DISABLED"),
            flag));
    }

    @PostMapping("/seed")
    @Transactional
    @Operation(summary = "Seed default feature flags — run once after first deployment")
    public ResponseEntity<ApiResponse<Void>> seed() {
        DEFAULT_FLAGS.forEach((key, description) -> {
            if (!flagRepository.existsByFlagKey(key)) {
                flagRepository.save(FeatureFlag.builder()
                    .flagKey(key)
                    .enabled(true)
                    .description(description)
                    .build());
            }
        });
        return ResponseEntity.ok(ApiResponse.success("Default feature flags seeded successfully"));
    }
}
