package com.startuphub.entity;

import com.startuphub.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

@Entity
@Table(
    name = "workflow_stages",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_stage_template_order",
            columnNames = {"workflow_template_id", "stage_order"}
        )
    },
    indexes = {
        @Index(name = "idx_stage_template", columnList = "workflow_template_id")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowStage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "stage_order", nullable = false)
    private int stageOrder;

    @Column(name = "estimated_days")
    private Integer estimatedDays;

    @Column(name = "sla_hours")
    private Integer slaHours;

    /**
     * Whether this stage requires explicit approval before the next stage starts.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean requiresApproval = false;

    /**
     * Which role is auto-assigned tasks for this stage.
     * Null means manual assignment is required.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "assignee_role", length = 20)
    private Role assigneeRole;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_template_id", nullable = false)
    private WorkflowTemplate workflowTemplate;

    /**
     * Denormalized for @Filter — same value as workflowTemplate.company_id.
     * Required so the tenantFilter can be applied directly on this table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
