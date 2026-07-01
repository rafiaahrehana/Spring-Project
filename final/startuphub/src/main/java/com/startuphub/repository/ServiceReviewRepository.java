package com.startuphub.repository;

import com.startuphub.entity.ServiceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ServiceReviewRepository extends JpaRepository<ServiceReview, Long> {

    Optional<ServiceReview> findByIdAndCompanyId(Long id, Long companyId);

    Optional<ServiceReview> findByServiceRequestIdAndClientId(Long serviceRequestId, Long clientId);

    Page<ServiceReview> findByCompanyId(Long companyId, Pageable pageable);

    Page<ServiceReview> findByCompanyIdAndHubServiceId(
        Long companyId, Long hubServiceId, Pageable pageable);

    boolean existsByServiceRequestIdAndClientId(Long serviceRequestId, Long clientId);

    @Query("SELECT AVG(r.rating) FROM ServiceReview r WHERE r.hubService.id = :serviceId AND r.published = true AND r.deleted = false")
    Optional<Double> findAverageRatingByServiceId(@Param("serviceId") Long serviceId);

    @Query("SELECT AVG(r.rating) FROM ServiceReview r WHERE r.company.id = :companyId AND r.published = true AND r.deleted = false")
    Optional<Double> findAverageRatingByCompanyId(@Param("companyId") Long companyId);
}
