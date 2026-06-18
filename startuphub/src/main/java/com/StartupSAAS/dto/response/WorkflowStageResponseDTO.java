package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.WorkflowStageType;
import lombok.Data;

@Data
public class WorkflowStageResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Integer stageOrder;
    private WorkflowStageType stageType;
    private Integer estimatedDays;
    private Long workflowTemplateId;
}
