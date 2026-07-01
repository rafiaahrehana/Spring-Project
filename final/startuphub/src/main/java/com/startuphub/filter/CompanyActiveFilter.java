package com.startuphub.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.startuphub.enums.CompanyStatus;
import com.startuphub.exception.ErrorResponse;
import com.startuphub.repository.CompanyRepository;
import com.startuphub.security.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Blocks API calls from companies with status SUSPENDED or DEACTIVATED.
 *
 * Runs after JwtAuthFilter — TenantContext is already populated when this fires.
 * Platform users (SUPER_ADMIN/SYSTEM_ADMIN) have no companyId and are not checked.
 *
 * Only executes when TenantContext.hasTenant() is true, meaning the request
 * is authenticated and belongs to a tenant company.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyActiveFilter extends OncePerRequestFilter {

    private final CompanyRepository companyRepository;
    private final CompanyStatusCache companyStatusCache;
    private final ObjectMapper      objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        if (!TenantContext.hasTenant()) {
            chain.doFilter(request, response);
            return;
        }

        Long companyId = TenantContext.getCompanyId();

        boolean blocked = companyStatusCache.isBlockedStatus(companyId);

        if (blocked) {
            log.warn("Request blocked — company {} is suspended or deactivated", companyId);
            sendError(response, request.getRequestURI());
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Account Suspended",
            "Your account has been suspended. Please contact support or upgrade your plan.",
            path,
            LocalDateTime.now(),
            null
        );
        objectMapper.writeValue(response.getWriter(), body);
    }
}
