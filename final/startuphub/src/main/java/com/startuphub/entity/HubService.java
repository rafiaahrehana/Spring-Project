package com.startuphub.entity;

import com.startuphub.enums.ServicePriceType;
import com.startuphub.enums.ServiceRequestPriority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;

@Entity
@Table(
    name = "hub_services",
    indexes = {
        @Index(name = "idx_hubservice_company",   columnList = "company_id"),
        @Index(name = "idx_hubservice_category",  columnList = "category_id"),
        @Index(name = "idx_hubservice_active",    columnList = "company_id, active")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 150)
    private String nameBn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String descriptionBn;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_type", length = 10)
    @Builder.Default
    private ServicePriceType priceType = ServicePriceType.FIXED;

    @Column(name = "estimated_days")
    private Integer estimatedDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_priority", length = 10)
    @Builder.Default
    private ServiceRequestPriority defaultPriority = ServiceRequestPriority.NORMAL;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * Category FK is nullable — service can be uncategorised temporarily.
     * Angular enforces category selection in the UI.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ServiceCategory category;

    /**
     * Workflow template is optional.
     * When null, service requests are handled manually (no auto-task creation).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_template_id")
    private WorkflowTemplate workflowTemplate;
}
