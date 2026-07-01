package com.startuphub.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter — runs once per request.
 *
 * On every authenticated request:
 *   1. Extract Bearer token from Authorization header
 *   2. Validate signature and expiry
 *   3. Load UserDetails from DB
 *   4. Set SecurityContext authentication
 *   5. Extract companyId from token and store in TenantContext
 *
 * TenantContext is ALWAYS cleared in the finally block to prevent
 * thread pool contamination between requests.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        // Always clear tenant context at the start of each request
        TenantContext.clear();

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);

            if (!jwtService.isValid(token)) {
                chain.doFilter(request, response);
                return;
            }

            String email = jwtService.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Set tenant context from JWT — O(1), no DB lookup
                Long companyId = jwtService.extractCompanyId(token);
                if (companyId != null) {
                    TenantContext.setCompanyId(companyId);
                    log.debug("Tenant context set: companyId={} user={}", companyId, email);
                }
            }

            chain.doFilter(request, response);

        } finally {
            // Critical: always clear, even if an exception is thrown
            TenantContext.clear();
        }
    }
}
