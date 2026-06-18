package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Task;
import com.StartupSAAS.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
        SELECT t FROM Task t
        LEFT JOIN FETCH t.serviceRequest sr
        LEFT JOIN FETCH sr.company
        LEFT JOIN FETCH t.assignedEmployee ae
        LEFT JOIN FETCH ae.user
        LEFT JOIN FETCH t.createdBy cb
        LEFT JOIN FETCH cb.user
        WHERE t.id = :id
    """)
    Optional<Task> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT t FROM Task t
        LEFT JOIN FETCH t.serviceRequest sr
        LEFT JOIN FETCH sr.company
        LEFT JOIN FETCH t.assignedEmployee ae
        LEFT JOIN FETCH ae.user
        LEFT JOIN FETCH t.createdBy cb
        LEFT JOIN FETCH cb.user
        WHERE t.serviceRequest.id = :serviceRequestId
    """)
    List<Task> findByServiceRequestId(@Param("serviceRequestId") Long serviceRequestId);

    List<Task> findByAssignedEmployeeId(Long employeeId);

    List<Task> findByServiceRequestIdAndStatus(Long serviceRequestId, TaskStatus status);
}
