package com.startuphub.enums;

/**
 * Platform-wide role enum.
 * Platform roles: SUPER_ADMIN, SYSTEM_ADMIN — no company_id in JWT.
 * Tenant roles: COMPANY_OWNER, ADMIN, EMPLOYEE, CLIENT — company_id embedded in JWT.
 * Phase 2 will add MANAGER and VIEWER.
 */
public enum Role {
    SUPER_ADMIN,
    SYSTEM_ADMIN,
    COMPANY_OWNER,
    ADMIN,
    EMPLOYEE,
    CLIENT
}
