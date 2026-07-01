package com.startuphub.config;

import com.startuphub.security.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

/**
 * AOP aspect — Layer 2 of the three-layer tenant isolation stack.
 *
 * Fires @Before every Spring Data Repository method call.
 * Enables the Hibernate "tenantFilter" on the current Session,
 * binding it to the companyId from TenantContext.
 *
 * Effect: every JPA query on tenant-scoped entities automatically
 * gets WHERE company_id = :companyId appended, even if the developer
 * forgets to filter explicitly.
 *
 * Phase 1 note: no tenant entities exist yet in Phase 1, so this
 * aspect is registered but will not match any repository calls.
 * It will become active from Phase 2 onwards when Company/Employee etc. are added.
 */
@Aspect
@Component
@Slf4j
public class TenantFilterConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("within(@org.springframework.stereotype.Repository *)")
    public void enableTenantFilter() {
        if (!TenantContext.hasTenant()) {
            return;
        }
        try {
            Session session = entityManager.unwrap(Session.class);
            Filter filter = session.enableFilter("tenantFilter");
            filter.setParameter("companyId", TenantContext.getCompanyId());
        } catch (Exception e) {
            // Some repositories (Token, AuditLog) have no tenantFilter defined.
            // This is expected — log at trace level only.
            log.trace("tenantFilter not applicable for this repository call: {}", e.getMessage());
        }
    }
}
