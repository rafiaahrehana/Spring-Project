package com.StartupSAAS.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class WorkflowTemplateRequestDTO {
    private String name;
    private String description;
    private Long companyId;
    private List<WorkflowStageRequestDTO> stages;
}
