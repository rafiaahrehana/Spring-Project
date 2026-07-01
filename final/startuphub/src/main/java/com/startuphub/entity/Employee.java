package com.startuphub.entity;

import com.startuphub.enums.EmploymentType;
import com.startuphub.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "employees",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_employee_user_company",
            columnNames = {"user_id", "company_id"})
    },
    indexes = {
        @Index(name = "idx_employee_company",    columnList = "company_id"),
        @Index(name = "idx_employee_user",       columnList = "user_id"),
        @Index(name = "idx_employee_department", columnList = "department_id")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identity link — one-to-one with User.
     * No cascade: User lifecycle managed explicitly in service layer.
     * Deleting Employee must explicitly deactivate the User.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // HR profile
    @Column(length = 100)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    private LocalDate dateOfBirth;
    private LocalDate hireDate;
    private LocalDate contractEndDate;

    // Salary snapshot — historical records in SalaryStructure (Phase 5)
    @Column(precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal houseRent;

    @Column(precision = 12, scale = 2)
    private BigDecimal medicalAllowance;

    @Column(precision = 12, scale = 2)
    private BigDecimal transportAllowance;

    // Sensitive PII — must be encrypted at rest (AES @Converter — Phase hardening)
    @Column(length = 100)
    private String bankName;

    @Column(length = 100)
    private String bankAccountNumber;

    // Emergency contact
    @Column(length = 100)
    private String emergencyContactName;

    @Column(length = 30)
    private String emergencyContactPhone;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
