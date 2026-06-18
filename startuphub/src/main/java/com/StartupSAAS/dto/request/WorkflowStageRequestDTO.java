package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.WorkflowStageType;
import lombok.Data;

@Data
public class WorkflowStageRequestDTO {
    private String name;
    private String description;
    private Integer stageOrder;
    private WorkflowStageType stageType;
    private Integer estimatedDays;
}
