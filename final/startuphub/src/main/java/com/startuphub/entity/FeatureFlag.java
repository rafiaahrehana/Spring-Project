package com.startuphub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Platform-wide feature toggles.
 *
 * Allows enabling/disabling features without a code deployment.
 * All flags are global — per-company overrides via TenantFeatureFlag (Phase 2).
 *
 * Default flags seeded at startup:
 *   ENABLE_BKASH       — show bKash payment option
 *   ENABLE_NAGAD       — show Nagad payment option
 *   ENABLE_REFERRAL    — toggle referral program
 *   ENABLE_AUTO_ASSIGN — auto-assign requests to staff
 *   ENABLE_PACKAGES    — show service bundles
 *   MAINTENANCE_MODE   — block all requests, show maintenance page
 *
 * Does NOT extend BaseEntity — flags should not be soft-deleted.
 * Uses its own audit timestamps for clarity.
 */
@Entity
@Table(
    name = "feature_flags",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_feature_flag_key", columnNames = "flag_key")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flag_key", nullable = false, length = 100)
    private String flagKey;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
