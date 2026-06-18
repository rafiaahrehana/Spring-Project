package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.WorkflowTemplateRequestDTO;
import com.StartupSAAS.dto.response.WorkflowTemplateResponseDTO;

import java.util.List;

public interface WorkflowTemplateService {

    WorkflowTemplateResponseDTO create(WorkflowTemplateRequestDTO dto);
    List<WorkflowTemplateResponseDTO> getAll();
    WorkflowTemplateResponseDTO getById(Long id);
    List<WorkflowTemplateResponseDTO> getByCompany(Long companyId);
    WorkflowTemplateResponseDTO update(Long id, WorkflowTemplateRequestDTO dto);
    void delete(Long id);
}
