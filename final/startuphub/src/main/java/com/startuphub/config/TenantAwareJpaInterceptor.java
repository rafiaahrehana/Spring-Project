package com.startuphub.config;

import com.startuphub.security.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * Hibernate StatementInspector — Layer 3 tenant isolation audit.
 *
 * Intercepts every SQL statement before execution.
 * In DEBUG mode logs the first 120 chars of each SQL with the tenant ID.
 * Useful for detecting cross-tenant query bugs in development.
 *
 * Registered in application.yml:
 *   spring.jpa.properties.hibernate.session_factory.statement_inspector:
 *     com.startuphub.config.TenantAwareJpaInterceptor
 *
 * Does NOT modify SQL — that is the job of @Filter (Layer 2)
 * and PostgreSQL RLS (Layer 3). This is an observability hook only.
 */
@Slf4j
public class TenantAwareJpaInterceptor implements StatementInspector {

    @Override
    public String inspect(String sql) {
        if (TenantContext.hasTenant() && log.isTraceEnabled()) {
            log.trace("[tenant={}] SQL: {}",
                TenantContext.getCompanyId(),
                sql.substring(0, Math.min(120, sql.length())));
        }
        return sql;
    }
}
