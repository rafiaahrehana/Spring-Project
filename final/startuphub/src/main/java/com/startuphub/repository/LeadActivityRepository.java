package com.startuphub.repository;

import com.startuphub.entity.LeadActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeadActivityRepository extends JpaRepository<LeadActivity, Long> {

    Optional<LeadActivity> findByIdAndCompanyId(Long id, Long companyId);

    Page<LeadActivity> findByLeadIdOrderByActivityAtDesc(Long leadId, Pageable pageable);
}
