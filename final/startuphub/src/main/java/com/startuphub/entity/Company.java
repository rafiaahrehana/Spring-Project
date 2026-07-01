package com.startuphub.entity;

import com.startuphub.enums.CompanyStatus;
import com.startuphub.enums.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The tenant record — one Company is one tenant.
 *
 * Every authenticated non-platform request carries this entity's ID
 * in the JWT (companyId claim). The ID is the tenant boundary.
 *
 * Ownership model:
 *   Company.owner → User (COMPANY_OWNER role)
 *   This is a @ManyToOne FK — one user can only own one company
 *   in the current design. Multi-ownership is a Phase 3 concern.
 *
 *   CASCADE: deleting a Company soft-deletes the owner User via service layer.
 *   We do NOT cascade at JPA level to preserve the explicit lifecycle contract.
 *
 * Subdomain:
 *   Must be globally unique. Used for tenant resolution in multi-tenant
 *   setups where the subdomain identifies the company.
 *   @Index on subdomain is critical — every tenant resolution hits this column.
 *
 * Subscription lifecycle:
 *   Registration     → status = PENDING_VERIFICATION
 *   Email verified   → status = TRIAL, subscriptionEnd = today + 14 days
 *   Plan purchased   → status = ACTIVE, subscriptionEnd updated
 *   Subscription ends → scheduler sets status = SUSPENDED
 *   Admin action     → status = DEACTIVATED
 *
 * trialReminderSentAt replaces the old boolean trialReminderSent flag.
 * Using a timestamp allows checking if a reminder was sent recently
 * regardless of subscription renewals.
 *
 * Multi-tenancy: Company is NOT filtered by tenantFilter (it IS the tenant).
 * AuditLog, Notification, and other platform entities reference Company by FK.
 */
@Entity
@Table(
    name = "companies",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_company_subdomain", columnNames = "subdomain")
    },
    indexes = {
        // Critical — every tenant resolution queries by subdomain
        @Index(name = "idx_company_subdomain",        columnList = "subdomain"),
        @Index(name = "idx_company_status",           columnList = "status"),
        @Index(name = "idx_company_subscription_end", columnList = "subscription_end")
    }
)
// No @FilterDef here — Company IS the tenant, it is never filtered by company_id
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String companyName;

    @Column(nullable = false, length = 100)
    private String subdomain;

    @Column(length = 255)
    private String companyEmail;

    @Column(length = 30)
    private String companyPhone;

    @Column(length = 255)
    private String website;

    @Column(length = 255)
    private String address;

    // ── Subscription ──────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    @Builder.Default
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.FREE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private CompanyStatus status = CompanyStatus.PENDING_VERIFICATION;

    @Column(name = "subscription_start")
    private LocalDate subscriptionStart;

    @Column(name = "subscription_end")
    private LocalDate subscriptionEnd;

    /**
     * Replaces the old boolean trialReminderSent flag.
     * Null means no reminder has been sent yet.
     * Non-null means a reminder was sent at this timestamp.
     */
    @Column(name = "trial_reminder_sent_at")
    private LocalDateTime trialReminderSentAt;

    // ── Branding ──────────────────────────────────────────────────

    private String logo;

    @Column(length = 7)
    private String primaryColor;

    @Column(length = 7)
    private String secondaryColor;

    @Column(length = 255)
    private String tagline;

    // ── Owner ─────────────────────────────────────────────────────

    /**
     * The COMPANY_OWNER user who registered this company.
     * FetchType.LAZY — do not load User on every Company fetch.
     * No cascade — lifecycle is managed explicitly in the service layer.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // ── Computed helpers ──────────────────────────────────────────

    public boolean isTrialExpired() {
        return subscriptionEnd != null && LocalDate.now().isAfter(subscriptionEnd);
    }

    public boolean isOperational() {
        return status == CompanyStatus.TRIAL || status == CompanyStatus.ACTIVE;
    }
}
