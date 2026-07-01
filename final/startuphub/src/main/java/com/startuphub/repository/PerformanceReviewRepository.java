package com.startuphub.repository;

import com.startuphub.entity.PerformanceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {

    Optional<PerformanceReview> findByIdAndCompanyId(Long id, Long companyId);

    Page<PerformanceReview> findByCompanyId(Long companyId, Pageable pageable);

    Page<PerformanceReview> findByCompanyIdAndEmployeeId(
        Long companyId, Long employeeId, Pageable pageable);
}
