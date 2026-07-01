package com.startuphub.entity;

import com.startuphub.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

@Entity
@Table(
    name = "leave_balances",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_leave_balance_employee_type_year",
            columnNames = {"employee_id", "leave_type", "year"})
    },
    indexes = {
        @Index(name = "idx_leave_balance_company",  columnList = "company_id"),
        @Index(name = "idx_leave_balance_employee", columnList = "employee_id")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false, length = 20)
    private LeaveType leaveType;

    @Column(nullable = false)
    private int year;

    @Column(name = "entitled_days", nullable = false)
    private int entitledDays;

    @Column(name = "used_days", nullable = false)
    @Builder.Default
    private int usedDays = 0;

    @Column(name = "pending_days", nullable = false)
    @Builder.Default
    private int pendingDays = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public int getRemainingDays() {
        return entitledDays - usedDays - pendingDays;
    }
}
