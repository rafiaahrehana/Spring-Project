package com.startuphub.entity;

import com.startuphub.enums.WebhookStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records every inbound webhook event from payment gateways.
 *
 * Supports:
 *   - Idempotency via unique transactionId — prevents duplicate processing
 *   - Retry state machine: RECEIVED → PROCESSED or FAILED → DEAD
 *   - Exponential backoff via nextRetryAt: 5m → 30m → 2h → 8h → 24h → DEAD
 *
 * Does NOT extend BaseEntity — webhook logs must never be soft-deleted.
 *
 * payload stored as TEXT (production: use JSON column type for queryability).
 *
 * Cleanup: scheduled job should delete PROCESSED records older than 30 days.
 */
@Entity
@Table(
    name = "webhook_logs",
    indexes = {
        @Index(name = "idx_webhook_transaction_id", columnList = "transaction_id"),
        @Index(name = "idx_webhook_status_retry",   columnList = "status, next_retry_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "transaction_id", nullable = false, unique = true, length = 255)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private WebhookStatus status = WebhookStatus.RECEIVED;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "received_at", nullable = false, updatable = false)
    private LocalDateTime receivedAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;
}
