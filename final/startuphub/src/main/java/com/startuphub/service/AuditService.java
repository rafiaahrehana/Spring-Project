package com.startuphub.service;

import com.startuphub.entity.AuditLog;
import com.startuphub.entity.User;
import com.startuphub.enums.AuditAction;
import com.startuphub.enums.AuditEntityType;
import com.startuphub.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Records all significant system actions into audit_logs.
 *
 * @Async ensures audit logging never slows down the main request path.
 *
 * IP resolution:
 *   Checks X-Forwarded-For first (for load balancer / reverse proxy setups).
 *   Falls back to getRemoteAddr() if no proxy header is present.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional
    public void log(AuditEntityType entityType,
                    Long entityId,
                    AuditAction action,
                    String oldValue,
                    String newValue,
                    User performedBy,
                    Long companyId) {
        AuditLog entry = AuditLog.builder()
            .entityType(entityType)
            .entityId(entityId)
            .action(action)
            .oldValue(oldValue)
            .newValue(newValue)
            .performedBy(performedBy)
            .companyId(companyId)
            .ipAddress(resolveClientIp())
            .build();
        auditLogRepository.save(entry);
    }

    @Async
    @Transactional
    public void logLogin(User user, Long companyId) {
        AuditLog entry = AuditLog.builder()
            .entityType(AuditEntityType.USER)
            .entityId(user.getId())
            .action(AuditAction.LOGIN)
            .newValue(user.getEmail())
            .performedBy(user)
            .companyId(companyId)
            .ipAddress(resolveClientIp())
            .build();
        auditLogRepository.save(entry);
    }

    @Async
    @Transactional
    public void logLogout(User user, Long companyId) {
        AuditLog entry = AuditLog.builder()
            .entityType(AuditEntityType.USER)
            .entityId(user.getId())
            .action(AuditAction.LOGOUT)
            .performedBy(user)
            .companyId(companyId)
            .ipAddress(resolveClientIp())
            .build();
        auditLogRepository.save(entry);
    }

    private String resolveClientIp() {
        try {
            ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attrs.getRequest();
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                return xff.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
