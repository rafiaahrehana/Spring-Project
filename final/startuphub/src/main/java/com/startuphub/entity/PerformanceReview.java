package com.startuphub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.time.LocalDate;

@Entity
@Table(
    name = "performance_reviews",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_perf_review_employee_period",
            columnNames = {"employee_id", "review_period_start", "review_period_end"})
    },
    indexes = {
        @Index(name = "idx_perf_review_company",  columnList = "company_id"),
        @Index(name = "idx_perf_review_employee", columnList = "employee_id")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_period_start", nullable = false)
    private LocalDate reviewPeriodStart;

    @Column(name = "review_period_end", nullable = false)
    private LocalDate reviewPeriodEnd;

    // Scores 1-5
    @Column(name = "score_work_quality")
    private Integer scoreWorkQuality;

    @Column(name = "score_productivity")
    private Integer scoreProductivity;

    @Column(name = "score_communication")
    private Integer scoreCommunication;

    @Column(name = "score_teamwork")
    private Integer scoreTeamwork;

    @Column(name = "score_initiative")
    private Integer scoreInitiative;

    @Column(name = "score_punctuality")
    private Integer scorePunctuality;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "areas_for_improvement", columnDefinition = "TEXT")
    private String areasForImprovement;

    @Column(name = "goals_for_next_period", columnDefinition = "TEXT")
    private String goalsForNextPeriod;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(nullable = false)
    @Builder.Default
    private boolean finalised = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id", nullable = false)
    private Employee reviewedBy;
}
