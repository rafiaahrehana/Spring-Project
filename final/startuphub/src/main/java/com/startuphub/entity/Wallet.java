package com.startuphub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;

@Entity
@Table(
    name = "wallets",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_wallet_company", columnNames = "company_id")
    },
    indexes = {
        @Index(name = "idx_wallet_company", columnList = "company_id")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cash balance — funds added by payment or admin top-up.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Credit balance — from refunds, referrals, and promotions.
     * Replaces TenantCredit entity per architecture baseline.
     */
    @Column(name = "credit_balance", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal creditBalance = BigDecimal.ZERO;

    @Column(length = 5)
    @Builder.Default
    private String currency = "BDT";

    /**
     * Ownership: One wallet per company.
     * unique = true enforces the 1:1 at DB level.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public BigDecimal getTotalAvailable() {
        return balance.add(creditBalance);
    }
}
