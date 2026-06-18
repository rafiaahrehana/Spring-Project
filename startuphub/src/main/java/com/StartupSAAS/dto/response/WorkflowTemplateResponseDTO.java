package com.StartupSAAS.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkflowTemplateResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;

    // Flattened company
    private Long companyId;
    private String companyName;

    // Ordered stages
    private List<WorkflowStageResponseDTO> stages;
}
