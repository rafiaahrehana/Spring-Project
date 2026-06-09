package com.saas.luminex.repository;

import com.saas.luminex.entity.ServiceRequest;
import com.saas.luminex.entity.User;
import com.saas.luminex.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    Page<ServiceRequest> findByClient(User client, Pageable pageable);

    Page<ServiceRequest> findByAssignedEmployee(User employee, Pageable pageable);

    Page<ServiceRequest> findByStatus(RequestStatus status, Pageable pageable);

    List<ServiceRequest> findByAssignedEmployeeAndStatus(User employee, RequestStatus status);

    @Query("SELECT COUNT(sr) FROM ServiceRequest sr WHERE sr.status = :status")
    long countByStatus(RequestStatus status);

    @Query("SELECT COUNT(sr) FROM ServiceRequest sr WHERE sr.client = :client AND sr.status = :status")
    long countByClientAndStatus(User client, RequestStatus status);
}
