package com.startuphub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Filter;

@Entity
@Table(
    name = "service_reviews",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_review_request_client",
            columnNames = {"service_request_id", "client_id"})
    },
    indexes = {
        @Index(name = "idx_review_company",  columnList = "company_id"),
        @Index(name = "idx_review_request",  columnList = "service_request_id"),
        @Index(name = "idx_review_service",  columnList = "hub_service_id")
    }
)
@Check(constraints = "rating >= 1 AND rating <= 5")
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    @Builder.Default
    private boolean published = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id", nullable = false)
    private ServiceRequest serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_service_id", nullable = false)
    private HubService hubService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
