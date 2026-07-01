package com.startuphub.entity;

import com.startuphub.enums.EmploymentType;
import com.startuphub.enums.JobPostingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "job_postings",
    indexes = {
        @Index(name = "idx_job_company",  columnList = "company_id"),
        @Index(name = "idx_job_status",   columnList = "company_id, status"),
        @Index(name = "idx_job_deadline", columnList = "deadline")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    /**
     * Free-text job title — replaces the hardcoded Designation enum.
     * Allows any company to define its own job title vocabulary.
     */
    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 20)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private JobPostingStatus status = JobPostingStatus.DRAFT;

    @Column(nullable = false)
    @Builder.Default
    private int vacancies = 1;

    @Column(name = "salary_min", precision = 12, scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "salary_max", precision = 12, scale = 2)
    private BigDecimal salaryMax;

    private LocalDate deadline;

    @Column(nullable = false)
    @Builder.Default
    private boolean remote = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private Employee createdBy;
}
