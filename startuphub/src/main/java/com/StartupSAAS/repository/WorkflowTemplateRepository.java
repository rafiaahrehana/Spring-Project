package com.StartupSAAS.repository;

import com.StartupSAAS.entity.WorkflowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowTemplateRepository extends JpaRepository<WorkflowTemplate, Long> {

    @Query("""
        SELECT wt FROM WorkflowTemplate wt
        LEFT JOIN FETCH wt.company
        LEFT JOIN FETCH wt.stages
        WHERE wt.id = :id
    """)
    Optional<WorkflowTemplate> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT wt FROM WorkflowTemplate wt
        LEFT JOIN FETCH wt.company
        LEFT JOIN FETCH wt.stages
    """)
    List<WorkflowTemplate> findAllWithDetails();

    @Query("""
        SELECT wt FROM WorkflowTemplate wt
        LEFT JOIN FETCH wt.company
        LEFT JOIN FETCH wt.stages
        WHERE wt.company.id = :companyId
    """)
    List<WorkflowTemplate> findByCompanyId(@Param("companyId") Long companyId);
}
