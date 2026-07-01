package com.startuphub.repository;

import com.startuphub.entity.WorkflowStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowStageRepository extends JpaRepository<WorkflowStage, Long> {

    List<WorkflowStage> findByWorkflowTemplateIdOrderByStageOrderAsc(Long templateId);

    Optional<WorkflowStage> findByIdAndCompanyId(Long id, Long companyId);

    boolean existsByWorkflowTemplateIdAndStageOrder(Long templateId, int stageOrder);
}
