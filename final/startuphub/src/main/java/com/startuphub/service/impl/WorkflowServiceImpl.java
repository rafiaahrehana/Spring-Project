package com.startuphub.service.impl;

import com.startuphub.dto.request.WorkflowStageRequest;
import com.startuphub.dto.request.WorkflowTemplateRequest;
import com.startuphub.dto.response.WorkflowStageResponse;
import com.startuphub.dto.response.WorkflowTemplateResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.WorkflowStage;
import com.startuphub.entity.WorkflowTemplate;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.WorkflowMapper;
import com.startuphub.repository.WorkflowStageRepository;
import com.startuphub.repository.WorkflowTemplateRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowTemplateRepository templateRepository;
    private final WorkflowStageRepository    stageRepository;
    private final SecurityUtil               securityUtil;

    // ── Templates ─────────────────────────────────────────────────

    @Override
    @Transactional
    public WorkflowTemplateResponse createTemplate(WorkflowTemplateRequest request) {
        Long companyId = requireCompanyId();
        if (templateRepository.existsByCompanyIdAndName(companyId, request.name())) {
            throw new BadRequestException("A workflow template named '" + request.name() + "' already exists");
        }
        WorkflowTemplate template = WorkflowTemplate.builder()
            .name(request.name())
            .description(request.description())
            .company(companyRef(companyId))
            .build();
        templateRepository.save(template);
        log.info("WorkflowTemplate created: '{}' company={}", template.getName(), companyId);
        return WorkflowMapper.toResponse(template);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowTemplateResponse getTemplateById(Long id) {
        return WorkflowMapper.toResponse(findTemplate(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkflowTemplateResponse> listTemplates(Pageable pageable) {
        return templateRepository.findByCompanyId(requireCompanyId(), pageable)
            .map(WorkflowMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowTemplateResponse> listActiveTemplates() {
        return templateRepository.findByCompanyIdAndActiveTrue(requireCompanyId())
            .stream().map(WorkflowMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public WorkflowTemplateResponse updateTemplate(Long id, WorkflowTemplateRequest request) {
        Long companyId = requireCompanyId();
        WorkflowTemplate template = findTemplate(id);
        if (!template.getName().equals(request.name())
                && templateRepository.existsByCompanyIdAndName(companyId, request.name())) {
            throw new BadRequestException("A workflow template named '" + request.name() + "' already exists");
        }
        template.setName(request.name());
        if (request.description() != null) template.setDescription(request.description());
        template.setVersion(template.getVersion() + 1);
        return WorkflowMapper.toResponse(template);
    }

    @Override
    @Transactional
    public WorkflowTemplateResponse toggleTemplate(Long id) {
        WorkflowTemplate template = findTemplate(id);
        template.setActive(!template.isActive());
        return WorkflowMapper.toResponse(template);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        WorkflowTemplate template = findTemplate(id);
        template.softDelete();
        log.info("WorkflowTemplate soft-deleted: id={}", id);
    }

    // ── Stages ────────────────────────────────────────────────────

    @Override
    @Transactional
    public WorkflowStageResponse addStage(Long templateId, WorkflowStageRequest request) {
        Long companyId = requireCompanyId();
        WorkflowTemplate template = findTemplate(templateId);

        if (stageRepository.existsByWorkflowTemplateIdAndStageOrder(
                templateId, request.stageOrder())) {
            throw new BadRequestException(
                "Stage order " + request.stageOrder() + " is already taken in this workflow");
        }

        WorkflowStage stage = WorkflowStage.builder()
            .name(request.name())
            .stageOrder(request.stageOrder())
            .estimatedDays(request.estimatedDays())
            .slaHours(request.slaHours())
            .requiresApproval(request.requiresApproval())
            .assigneeRole(request.assigneeRole())
            .description(request.description())
            .workflowTemplate(template)
            .company(companyRef(companyId))
            .build();

        stageRepository.save(stage);
        template.setVersion(template.getVersion() + 1);
        template.getStages().add(stage);

        log.info("WorkflowStage added: '{}' order={} templateId={}", stage.getName(), stage.getStageOrder(), templateId);
        return WorkflowMapper.toStageResponse(stage);
    }

    @Override
    @Transactional
    public WorkflowStageResponse updateStage(Long templateId, Long stageId, WorkflowStageRequest request) {
        findTemplate(templateId);
        WorkflowStage stage = stageRepository.findById(stageId)
            .filter(s -> s.getWorkflowTemplate().getId().equals(templateId))
            .orElseThrow(() -> new ResourceNotFoundException("Stage not found: " + stageId));

        if (stage.getStageOrder() != request.stageOrder()
                && stageRepository.existsByWorkflowTemplateIdAndStageOrder(
                    templateId, request.stageOrder())) {
            throw new BadRequestException(
                "Stage order " + request.stageOrder() + " is already taken");
        }

        stage.setName(request.name());
        stage.setStageOrder(request.stageOrder());
        stage.setEstimatedDays(request.estimatedDays());
        stage.setSlaHours(request.slaHours());
        stage.setRequiresApproval(request.requiresApproval());
        stage.setAssigneeRole(request.assigneeRole());
        if (request.description() != null) stage.setDescription(request.description());

        WorkflowTemplate template = stage.getWorkflowTemplate();
        template.setVersion(template.getVersion() + 1);

        return WorkflowMapper.toStageResponse(stage);
    }

    @Override
    @Transactional
    public void deleteStage(Long templateId, Long stageId) {
        WorkflowTemplate template = findTemplate(templateId);
        WorkflowStage stage = stageRepository.findById(stageId)
            .filter(s -> s.getWorkflowTemplate().getId().equals(templateId))
            .orElseThrow(() -> new ResourceNotFoundException("Stage not found: " + stageId));
        template.getStages().remove(stage);
        stageRepository.delete(stage);
        template.setVersion(template.getVersion() + 1);
    }

    // ── Private helpers ───────────────────────────────────────────

    private WorkflowTemplate findTemplate(Long id) {
        return templateRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Workflow template not found: " + id));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }

    private Company companyRef(Long companyId) {
        Company c = new Company();
        c.setId(companyId);
        return c;
    }
}
