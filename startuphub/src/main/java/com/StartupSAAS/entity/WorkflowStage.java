package com.StartupSAAS.entity;

import com.StartupSAAS.enums.WorkflowStageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workflow_stages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkflowStage extends BaseEntity {

    @Column(nullable = false)
    private String name;   // e.g. "Document Collection"

    @Column(columnDefinition = "TEXT")
    private String description;

    // Order in the workflow
    private Integer stageOrder;

    @Enumerated(EnumType.STRING)
    private WorkflowStageType stageType = WorkflowStageType.PENDING;

    // Estimated days for this stage
    private Integer estimatedDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_template_id", nullable = false)
    private WorkflowTemplate workflowTemplate;
}
