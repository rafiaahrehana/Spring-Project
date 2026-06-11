package com.saas.luminex.service.impl;

import com.saas.luminex.entity.AuditLog;
import com.saas.luminex.entity.User;
import com.saas.luminex.repository.AuditLogRepository;
import com.saas.luminex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final SecurityUtil securityUtil;

    @Async
    @Transactional
    public void log(String action, String details) {
        try {
            User user = securityUtil.getCurrentUser();
            AuditLog log = AuditLog.builder()
                    .userId(user.getId())
                    .userName(user.getName())
                    .userRole(user.getRole())
                    .action(action)
                    .details(details)
                    .build();
            auditLogRepository.save(log);
        } catch (Exception ignored) {
            // Audit failures must never break the main flow
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}
