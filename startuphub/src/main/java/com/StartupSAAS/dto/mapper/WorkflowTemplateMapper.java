package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.WorkflowStageResponseDTO;
import com.StartupSAAS.dto.response.WorkflowTemplateResponseDTO;
import com.StartupSAAS.entity.WorkflowStage;
import com.StartupSAAS.entity.WorkflowTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WorkflowTemplateMapper {

    public static WorkflowTemplateResponseDTO toDTO(WorkflowTemplate wt) {

        WorkflowTemplateResponseDTO dto = new WorkflowTemplateResponseDTO();
        dto.setId(wt.getId());
        dto.setName(wt.getName());
        dto.setDescription(wt.getDescription());
        dto.setActive(wt.getActive());
        dto.setCreatedAt(wt.getCreatedAt());

        if (wt.getCompany() != null) {
            dto.setCompanyId(wt.getCompany().getId());
            dto.setCompanyName(wt.getCompany().getName());
        }

        if (wt.getStages() == null || wt.getStages().isEmpty()) {
            dto.setStages(Collections.emptyList());
        } else {
            dto.setStages(wt.getStages().stream()
                    .map(WorkflowTemplateMapper::toStageDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static WorkflowStageResponseDTO toStageDTO(WorkflowStage stage) {
        WorkflowStageResponseDTO dto = new WorkflowStageResponseDTO();
        dto.setId(stage.getId());
        dto.setName(stage.getName());
        dto.setDescription(stage.getDescription());
        dto.setStageOrder(stage.getStageOrder());
        dto.setStageType(stage.getStageType());
        dto.setEstimatedDays(stage.getEstimatedDays());
        if (stage.getWorkflowTemplate() != null)
            dto.setWorkflowTemplateId(stage.getWorkflowTemplate().getId());
        return dto;
    }
}
