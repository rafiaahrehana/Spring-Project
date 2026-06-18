package com.StartupSAAS.repository;

import com.StartupSAAS.entity.HubService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HubServiceRepository extends JpaRepository<HubService, Long> {

    @Query("""
        SELECT s FROM HubService s
        LEFT JOIN FETCH s.company
        LEFT JOIN FETCH s.workflowTemplate wt
        LEFT JOIN FETCH wt.stages
        WHERE s.id = :id
    """)
    Optional<HubService> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT s FROM HubService s
        LEFT JOIN FETCH s.company
        LEFT JOIN FETCH s.workflowTemplate wt
        LEFT JOIN FETCH wt.stages
    """)
    List<HubService> findAllWithDetails();

    @Query("""
        SELECT s FROM HubService s
        LEFT JOIN FETCH s.company
        LEFT JOIN FETCH s.workflowTemplate wt
        LEFT JOIN FETCH wt.stages
        WHERE s.company.id = :companyId
    """)
    List<HubService> findByCompanyId(@Param("companyId") Long companyId);

    List<HubService> findByCompanyIdAndActiveTrue(Long companyId);
}
