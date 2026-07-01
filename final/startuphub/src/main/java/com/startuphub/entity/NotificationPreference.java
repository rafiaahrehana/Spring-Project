package com.startuphub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Per-user notification preferences.
 *
 * Scope: user-level, NOT company-level.
 * A user's preference to receive emails is personal — it does not belong
 * to the company. This entity has NO company_id and no @Filter.
 *
 * Lifecycle: created automatically when a User is activated (verifyEmail).
 * Each user has exactly one preference record (unique constraint on user_id).
 *
 * Design note: 8 boolean columns cover Phase 2 notification types.
 * Phase 3 may replace this with a key-value child table for extensibility,
 * but for MVP the flat structure is simpler and faster to query.
 *
 * Does not extend BaseEntity — preferences are not soft-deleted.
 * If a user is soft-deleted, cascade in the service layer handles this.
 */
@Entity
@Table(
    name = "notification_preferences",
    indexes = {
        @Index(name = "idx_notif_pref_user", columnList = "user_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owning user — one preference record per user, enforced by unique constraint.
     * No cascade here — lifecycle managed in service layer.
     * FetchType.LAZY — don't load User on every preference fetch.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // ── Email notifications ───────────────────────────────────────

    @Column(nullable = false)
    @Builder.Default
    private boolean emailOnServiceRequest = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean emailOnStatusChange = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean emailOnInvoice = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean emailOnPayment = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean emailOnTaskAssigned = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean emailOnLeaveUpdate = true;

    // ── In-app notifications ──────────────────────────────────────

    @Column(nullable = false)
    @Builder.Default
    private boolean inAppOnServiceRequest = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean inAppOnStatusChange = true;

    // ── Marketing ─────────────────────────────────────────────────

    @Column(nullable = false)
    @Builder.Default
    private boolean emailMarketing = false;

    // ── Audit timestamps ──────────────────────────────────────────

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
