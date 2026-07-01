package com.startuphub.entity;

import com.startuphub.enums.LetterType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.time.LocalDate;

@Entity
@Table(
    name = "employment_letters",
    indexes = {
        @Index(name = "idx_letter_company",  columnList = "company_id"),
        @Index(name = "idx_letter_employee", columnList = "employee_id"),
        @Index(name = "idx_letter_type",     columnList = "company_id, letter_type")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentLetter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "letter_type", nullable = false, length = 30)
    private LetterType letterType;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "signed_by", length = 150)
    private String signedBy;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean issued = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;
}
