package com.startuphub.security;

/**
 * Thread-local store for the current tenant's company ID.
 *
 * Lifecycle:
 *   1. JwtAuthFilter extracts companyId from JWT on every request
 *   2. TenantContext.setCompanyId() stores it in ThreadLocal
 *   3. TenantFilterConfig AOP reads it before every repository call
 *   4. JwtAuthFilter finally block calls TenantContext.clear()
 *
 * SUPER_ADMIN / SYSTEM_ADMIN users have companyId = null.
 * hasTenant() returns false for platform-level users.
 *
 * Never call setCompanyId() from business logic — only from JwtAuthFilter.
 */
public final class TenantContext {

    private static final ThreadLocal<Long> COMPANY_ID = new ThreadLocal<>();

    private TenantContext() {}

    public static void setCompanyId(Long companyId) {
        COMPANY_ID.set(companyId);
    }

    public static Long getCompanyId() {
        return COMPANY_ID.get();
    }

    public static boolean hasTenant() {
        return COMPANY_ID.get() != null;
    }

    /**
     * Must be called after every request completes.
     * Failure to clear causes thread pool contamination —
     * the next request on the same thread inherits the previous tenant's ID.
     */
    public static void clear() {
        COMPANY_ID.remove();
    }
}
