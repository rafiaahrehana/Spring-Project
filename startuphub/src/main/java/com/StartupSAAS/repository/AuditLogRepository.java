package com.StartupSAAS.repository;

import com.StartupSAAS.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByCompanyIdOrderByPerformedAtDesc(Long companyId);
    List<AuditLog> findByEntityTypeAndEntityIdOrderByPerformedAtDesc(String entityType, Long entityId);
    List<AuditLog> findByPerformedByIdOrderByPerformedAtDesc(Long userId);
}
