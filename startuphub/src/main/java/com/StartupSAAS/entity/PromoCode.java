package com.StartupSAAS.entity;

import com.StartupSAAS.enums.DiscountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "promo_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PromoCode extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String code;  // e.g. "LAUNCH50"

    @Enumerated(EnumType.STRING)
    private DiscountType discountType = DiscountType.PERCENT;

    private Double discountValue;   // e.g. 50 (50%) or 500 (BDT 500)

    private Double minOrderAmount;  // minimum invoice total to apply

    private Integer maxUses;        // null = unlimited

    private Integer usedCount = 0;

    private LocalDate validFrom;

    private LocalDate validUntil;

    private Boolean active = true;

    // Which company issued this promo code
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
