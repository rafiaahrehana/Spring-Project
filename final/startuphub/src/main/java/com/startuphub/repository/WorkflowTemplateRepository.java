package com.startuphub.repository;

import com.startuphub.entity.WorkflowTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowTemplateRepository extends JpaRepository<WorkflowTemplate, Long> {

    Page<WorkflowTemplate> findByCompanyId(Long companyId, Pageable pageable);

    List<WorkflowTemplate> findByCompanyIdAndActiveTrue(Long companyId);

    Optional<WorkflowTemplate> findByIdAndCompanyId(Long id, Long companyId);

    boolean existsByCompanyIdAndName(Long companyId, String name);
}
