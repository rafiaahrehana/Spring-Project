package com.StartupSAAS.entity;

import com.StartupSAAS.enums.RecurringCycle;
import com.StartupSAAS.enums.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Subscription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    private RecurringCycle cycle = RecurringCycle.MONTHLY;

    private Double amount;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active = true;

    private Boolean autoRenew = true;

    // Which company this subscription belongs to
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;
}
