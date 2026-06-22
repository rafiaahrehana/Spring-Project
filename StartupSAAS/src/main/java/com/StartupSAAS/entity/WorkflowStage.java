package com.StartupSAAS.entity;

import com.StartupSAAS.enums.WorkflowStageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workflow_stages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer stageOrder;

    @Enumerated(EnumType.STRING)
    private WorkflowStageType stageType = WorkflowStageType.PENDING;

    private Integer estimatedDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_template_id", nullable = false)
    private WorkflowTemplate workflowTemplate;
}
