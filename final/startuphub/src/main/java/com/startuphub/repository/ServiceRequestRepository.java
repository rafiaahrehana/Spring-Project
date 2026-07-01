package com.startuphub.repository;

import com.startuphub.entity.ServiceRequest;
import com.startuphub.enums.ServiceRequestPriority;
import com.startuphub.enums.ServiceRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    Optional<ServiceRequest> findByIdAndCompanyId(Long id, Long companyId);

    Page<ServiceRequest> findByCompanyId(Long companyId, Pageable pageable);

    Page<ServiceRequest> findByCompanyIdAndStatus(
        Long companyId, ServiceRequestStatus status, Pageable pageable);

    Page<ServiceRequest> findByCompanyIdAndClientId(
        Long companyId, Long clientId, Pageable pageable);

    Page<ServiceRequest> findByCompanyIdAndAssignedEmployeeId(
        Long companyId, Long employeeId, Pageable pageable);

    long countByCompanyIdAndStatus(Long companyId, ServiceRequestStatus status);

    @Query("""
        SELECT r FROM ServiceRequest r
        WHERE r.company.id = :companyId
          AND r.slaDeadline < :now
          AND r.slaBreach = false
          AND r.status NOT IN :closedStatuses
          AND r.deleted = false
        """)
    List<ServiceRequest> findUnmarkedSlaBreaches(
        @Param("companyId") Long companyId,
        @Param("now") LocalDateTime now,
        @Param("closedStatuses") List<ServiceRequestStatus> closedStatuses
    );

    @Query("""
        SELECT r FROM ServiceRequest r
        WHERE r.slaDeadline < :now
          AND r.slaBreach = false
          AND r.status NOT IN :closedStatuses
          AND r.deleted = false
        """)
    List<ServiceRequest> findAllUnmarkedSlaBreaches(
        @Param("now") LocalDateTime now,
        @Param("closedStatuses") List<ServiceRequestStatus> closedStatuses
    );

    /**
     * Bulk UPDATE — marks all breached requests in one SQL statement.
     * Used by SlaBreachScheduler to avoid loading all entities into memory.
     */
    @Modifying
    @Query("""
        UPDATE ServiceRequest r SET r.slaBreach = true
        WHERE r.slaDeadline < :now
          AND r.slaBreach = false
          AND r.status NOT IN :closedStatuses
          AND r.deleted = false
        """)
    int bulkMarkSlaBreaches(
        @Param("now") LocalDateTime now,
        @Param("closedStatuses") List<ServiceRequestStatus> closedStatuses
    );
}
