package com.startuphub.service;

import com.startuphub.dto.request.WorkflowStageRequest;
import com.startuphub.dto.request.WorkflowTemplateRequest;
import com.startuphub.dto.response.WorkflowStageResponse;
import com.startuphub.dto.response.WorkflowTemplateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkflowService {

    WorkflowTemplateResponse createTemplate(WorkflowTemplateRequest request);

    WorkflowTemplateResponse getTemplateById(Long id);

    Page<WorkflowTemplateResponse> listTemplates(Pageable pageable);

    List<WorkflowTemplateResponse> listActiveTemplates();

    WorkflowTemplateResponse updateTemplate(Long id, WorkflowTemplateRequest request);

    WorkflowTemplateResponse toggleTemplate(Long id);

    void deleteTemplate(Long id);

    WorkflowStageResponse addStage(Long templateId, WorkflowStageRequest request);

    WorkflowStageResponse updateStage(Long templateId, Long stageId, WorkflowStageRequest request);

    void deleteStage(Long templateId, Long stageId);
}
