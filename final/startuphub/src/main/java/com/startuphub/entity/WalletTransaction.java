package com.startuphub.entity;

import com.startuphub.enums.WalletTransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable ledger entry for every wallet movement.
 * Does NOT extend BaseEntity — ledger records are never soft-deleted.
 * balanceAfter stores the running balance for audit trail and reconciliation.
 */
@Entity
@Table(
    name = "wallet_transactions",
    indexes = {
        @Index(name = "idx_wallet_tx_company",  columnList = "company_id, transacted_at"),
        @Index(name = "idx_wallet_tx_wallet",   columnList = "wallet_id"),
        @Index(name = "idx_wallet_tx_ref",      columnList = "reference")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WalletTransactionType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false, precision = 12, scale = 2)
    private BigDecimal balanceAfter;

    /**
     * Human-readable reference — e.g. "Invoice #INV-2025-001", "Refund REF-42".
     */
    @Column(length = 255)
    private String reference;

    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Timestamp is set by service layer, not entity constructor,
     * to allow deterministic testing and accurate ordering.
     */
    @Column(name = "transacted_at", nullable = false, updatable = false)
    private LocalDateTime transactedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    /**
     * Direct company_id for @Filter isolation.
     * Value always matches wallet.company_id.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
