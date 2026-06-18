package com.StartupSAAS.repository;

import com.StartupSAAS.entity.ServiceRequest;
import com.StartupSAAS.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    @Query("""
        SELECT sr FROM ServiceRequest sr
        LEFT JOIN FETCH sr.hubService hs
        LEFT JOIN FETCH hs.company
        LEFT JOIN FETCH sr.client cl
        LEFT JOIN FETCH cl.user
        LEFT JOIN FETCH sr.company
        LEFT JOIN FETCH sr.assignedEmployee ae
        LEFT JOIN FETCH ae.user
        WHERE sr.id = :id
    """)
    Optional<ServiceRequest> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT sr FROM ServiceRequest sr
        LEFT JOIN FETCH sr.hubService hs
        LEFT JOIN FETCH hs.company
        LEFT JOIN FETCH sr.client cl
        LEFT JOIN FETCH cl.user
        LEFT JOIN FETCH sr.company
        LEFT JOIN FETCH sr.assignedEmployee ae
        LEFT JOIN FETCH ae.user
    """)
    List<ServiceRequest> findAllWithDetails();

    @Query("""
        SELECT sr FROM ServiceRequest sr
        LEFT JOIN FETCH sr.hubService hs
        LEFT JOIN FETCH hs.company
        LEFT JOIN FETCH sr.client cl
        LEFT JOIN FETCH cl.user
        LEFT JOIN FETCH sr.company
        LEFT JOIN FETCH sr.assignedEmployee ae
        LEFT JOIN FETCH ae.user
        WHERE sr.company.id = :companyId
    """)
    List<ServiceRequest> findByCompanyId(@Param("companyId") Long companyId);

    @Query("""
        SELECT sr FROM ServiceRequest sr
        LEFT JOIN FETCH sr.hubService
        LEFT JOIN FETCH sr.client cl
        LEFT JOIN FETCH cl.user
        LEFT JOIN FETCH sr.company
        LEFT JOIN FETCH sr.assignedEmployee ae
        LEFT JOIN FETCH ae.user
        WHERE sr.client.id = :clientId
    """)
    List<ServiceRequest> findByClientId(@Param("clientId") Long clientId);

    @Query("""
        SELECT sr FROM ServiceRequest sr
        LEFT JOIN FETCH sr.hubService
        LEFT JOIN FETCH sr.client cl
        LEFT JOIN FETCH cl.user
        LEFT JOIN FETCH sr.company
        LEFT JOIN FETCH sr.assignedEmployee ae
        LEFT JOIN FETCH ae.user
        WHERE sr.assignedEmployee.id = :employeeId
    """)
    List<ServiceRequest> findByAssignedEmployeeId(@Param("employeeId") Long employeeId);

    List<ServiceRequest> findByCompanyIdAndStatus(Long companyId, RequestStatus status);
}
