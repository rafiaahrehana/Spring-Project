package com.startuphub.repository;

import com.startuphub.entity.Lead;
import com.startuphub.enums.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    Optional<Lead> findByIdAndCompanyId(Long id, Long companyId);

    Page<Lead> findByCompanyId(Long companyId, Pageable pageable);

    Page<Lead> findByCompanyIdAndStatus(Long companyId, LeadStatus status, Pageable pageable);

    Page<Lead> findByCompanyIdAndAssignedToId(Long companyId, Long employeeId, Pageable pageable);

    long countByCompanyIdAndStatus(Long companyId, LeadStatus status);

    @Query("SELECT COUNT(l) FROM Lead l WHERE l.company.id = :companyId AND l.status NOT IN ('WON','LOST','UNQUALIFIED') AND l.deleted = false")
    long countActiveByCompanyId(@Param("companyId") Long companyId);
}
