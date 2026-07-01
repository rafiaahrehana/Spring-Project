package com.startuphub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Filter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "departments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_dept_company_name", columnNames = {"company_id", "name"})
    },
    indexes = {
        @Index(name = "idx_dept_company", columnList = "company_id")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * Head of department — nullable FK to Employee.
     * Circular FK: Department ← Employee ← Department.
     * head_employee_id is added via ALTER TABLE after employees table is created.
     * In Flyway migrations: create departments (no head FK), create employees,
     * then ALTER TABLE departments ADD COLUMN head_employee_id BIGINT.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_employee_id")
    private Employee head;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();
}
