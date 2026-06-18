package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.WorkflowTemplateMapper;
import com.StartupSAAS.dto.request.WorkflowStageRequestDTO;
import com.StartupSAAS.dto.request.WorkflowTemplateRequestDTO;
import com.StartupSAAS.dto.response.WorkflowTemplateResponseDTO;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.WorkflowStage;
import com.StartupSAAS.entity.WorkflowTemplate;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.WorkflowTemplateRepository;
import com.StartupSAAS.service.WorkflowTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowTemplateServiceImpl implements WorkflowTemplateService {

    private final WorkflowTemplateRepository workflowTemplateRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    @Override
    public WorkflowTemplateResponseDTO create(WorkflowTemplateRequestDTO dto) {

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + dto.getCompanyId()));

        WorkflowTemplate template = new WorkflowTemplate();
        template.setName(dto.getName());
        template.setDescription(dto.getDescription());
        template.setCompany(company);
        template.setActive(true);

        // Build stages
        if (dto.getStages() != null && !dto.getStages().isEmpty()) {
            List<WorkflowStage> stages = new ArrayList<>();
            for (WorkflowStageRequestDTO stageDTO : dto.getStages()) {
                WorkflowStage stage = new WorkflowStage();
                stage.setName(stageDTO.getName());
                stage.setDescription(stageDTO.getDescription());
                stage.setStageOrder(stageDTO.getStageOrder());
                stage.setStageType(stageDTO.getStageType());
                stage.setEstimatedDays(stageDTO.getEstimatedDays());
                stage.setWorkflowTemplate(template);
                stages.add(stage);
            }
            template.setStages(stages);
        }

        WorkflowTemplate saved = workflowTemplateRepository.save(template);
        return WorkflowTemplateMapper.toDTO(
                workflowTemplateRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowTemplateResponseDTO> getAll() {
        return workflowTemplateRepository.findAllWithDetails()
                .stream().map(WorkflowTemplateMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowTemplateResponseDTO getById(Long id) {
        return WorkflowTemplateMapper.toDTO(
                workflowTemplateRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new RuntimeException(
                                "WorkflowTemplate not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowTemplateResponseDTO> getByCompany(Long companyId) {
        return workflowTemplateRepository.findByCompanyId(companyId)
                .stream().map(WorkflowTemplateMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public WorkflowTemplateResponseDTO update(Long id, WorkflowTemplateRequestDTO dto) {

        WorkflowTemplate template = workflowTemplateRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "WorkflowTemplate not found with id: " + id));

        if (dto.getName() != null)        template.setName(dto.getName());
        if (dto.getDescription() != null) template.setDescription(dto.getDescription());

        // Replace all stages if provided
        if (dto.getStages() != null && !dto.getStages().isEmpty()) {
            template.getStages().clear();
            for (WorkflowStageRequestDTO stageDTO : dto.getStages()) {
                WorkflowStage stage = new WorkflowStage();
                stage.setName(stageDTO.getName());
                stage.setDescription(stageDTO.getDescription());
                stage.setStageOrder(stageDTO.getStageOrder());
                stage.setStageType(stageDTO.getStageType());
                stage.setEstimatedDays(stageDTO.getEstimatedDays());
                stage.setWorkflowTemplate(template);
                template.getStages().add(stage);
            }
        }

        WorkflowTemplate saved = workflowTemplateRepository.save(template);
        return WorkflowTemplateMapper.toDTO(
                workflowTemplateRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    public void delete(Long id) {
        workflowTemplateRepository.deleteById(id);
    }
}
