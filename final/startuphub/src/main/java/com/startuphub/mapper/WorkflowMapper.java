package com.startuphub.mapper;

import com.startuphub.dto.response.WorkflowStageResponse;
import com.startuphub.dto.response.WorkflowTemplateResponse;
import com.startuphub.entity.WorkflowStage;
import com.startuphub.entity.WorkflowTemplate;

import java.util.List;

public final class WorkflowMapper {

    private WorkflowMapper() {}

    public static WorkflowStageResponse toStageResponse(WorkflowStage s) {
        return new WorkflowStageResponse(
            s.getId(),
            s.getName(),
            s.getStageOrder(),
            s.getEstimatedDays(),
            s.getSlaHours(),
            s.isRequiresApproval(),
            s.getAssigneeRole(),
            s.getDescription()
        );
    }

    public static WorkflowTemplateResponse toResponse(WorkflowTemplate t) {
        List<WorkflowStageResponse> stages = t.getStages() == null
            ? List.of()
            : t.getStages().stream().map(WorkflowMapper::toStageResponse).toList();
        return new WorkflowTemplateResponse(
            t.getId(),
            t.getName(),
            t.getDescription(),
            t.getVersion(),
            t.isActive(),
            stages,
            t.getCreatedAt(),
            t.getUpdatedAt()
        );
    }
}
