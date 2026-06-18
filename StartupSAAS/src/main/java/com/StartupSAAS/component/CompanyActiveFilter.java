package com.StartupSAAS.component;

import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.repository.CompanyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CompanyActiveFilter extends OncePerRequestFilter {

    private final CompanyRepository companyRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();

            if (principal instanceof User user) {
                if (user.getRole() == Role.SUPER_ADMIN) {
                    chain.doFilter(request, response);
                    return;
                }

                Company company = companyRepository.findByUserId(user.getId())
                        .orElse(null);

                if (company != null && !company.isActive()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\": \"Company subscription expired or inactive\"}");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }
}
