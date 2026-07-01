package com.startuphub.entity;

import com.startuphub.enums.AuditAction;
import com.startuphub.enums.AuditEntityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable record of every significant action in the system.
 *
 * Does NOT extend BaseEntity — audit logs must never be soft-deleted.
 *
 * company_id is stored as a plain Long in Phase 1 since Company entity
 * is not yet introduced. In Phase 2, this will become a @ManyToOne FK.
 *
 * performedBy is nullable for system-generated events (schedulers, etc.).
 *
 * ip_address captures the real client IP from X-Forwarded-For header.
 */
@Entity
@Table(
    name = "audit_logs",
    indexes = {
        @Index(name = "idx_audit_company_time", columnList = "company_id, performed_at"),
        @Index(name = "idx_audit_user",         columnList = "performed_by_id"),
        @Index(name = "idx_audit_entity",       columnList = "entity_type, entity_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 50)
    private AuditEntityType entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AuditAction action;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "performed_at", nullable = false, updatable = false)
    private LocalDateTime performedAt = LocalDateTime.now();

    /**
     * Phase 1: stored as plain Long — no FK to Company yet.
     * Phase 2: replaced with @ManyToOne Company reference.
     * Nullable — platform-level events (SUPER_ADMIN actions) have no company.
     */
    @Column(name = "company_id")
    private Long companyId;

    // Nullable — system-triggered events have no user actor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id")
    private User performedBy;
}
