package com.startuphub.entity;

import com.startuphub.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * In-app notification record for a single recipient.
 *
 * Delivery model:
 *   - Persisted here on creation (guaranteed delivery even if WebSocket is disconnected)
 *   - Pushed via WebSocket by NotificationService after persistence
 *   - Client polls GET /api/notifications on reconnect to catch missed notifications
 *
 * Tenant isolation:
 *   company_id is NOT NULL — every notification belongs to a specific tenant.
 *   Platform-level notifications (e.g. SUPER_ADMIN alerts) are handled separately.
 *
 * Does NOT extend BaseEntity — notifications are never soft-deleted.
 * They are marked read. Retention cleanup is handled by a scheduler.
 *
 * serviceRequestId is nullable — not every notification relates to a request
 * (e.g. LEAVE_APPROVED, PAYSLIP_READY are HR-scoped).
 */
@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notif_recipient_read",    columnList = "recipient_id, is_read"),
        @Index(name = "idx_notif_company",           columnList = "company_id"),
        @Index(name = "idx_notif_created",           columnList = "created_at"),
        @Index(name = "idx_notif_service_request",   columnList = "service_request_id")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * Link the Angular app should navigate to when notification is clicked.
     * e.g. /service-requests/42 or /hr/leave-requests/7
     */
    @Column(length = 500)
    private String actionUrl;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    /**
     * Nullable — HR notifications are not linked to a service request.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id")
    private ServiceRequest serviceRequest;
}
