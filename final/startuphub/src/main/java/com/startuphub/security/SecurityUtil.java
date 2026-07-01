package com.startuphub.security;

import com.startuphub.entity.User;
import com.startuphub.exception.UnauthorizedException;
import com.startuphub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility for accessing the currently authenticated user and tenant context.
 *
 * getCurrentUser() loads the User from DB using the email in SecurityContext.
 * getCurrentCompanyId() reads from TenantContext — zero DB cost.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("No authenticated user in context");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Authenticated user not found: " + email));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Reads the tenant's company ID from TenantContext (set from JWT).
     * Returns null for SUPER_ADMIN / SYSTEM_ADMIN.
     * O(1) — no DB lookup.
     */
    public Long getCurrentCompanyId() {
        return TenantContext.getCompanyId();
    }

    /**
     * Returns true for SUPER_ADMIN and SYSTEM_ADMIN users
     * who are not scoped to any company.
     */
    public boolean isPlatformAdmin() {
        return TenantContext.getCompanyId() == null;
    }
}
