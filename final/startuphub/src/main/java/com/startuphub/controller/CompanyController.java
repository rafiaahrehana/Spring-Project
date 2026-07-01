package com.startuphub.controller;

import com.startuphub.dto.request.RegisterCompanyRequest;
import com.startuphub.dto.request.UpdateCompanyRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.CompanyPublicResponse;
import com.startuphub.dto.response.CompanyResponse;
import com.startuphub.enums.CompanyStatus;
import com.startuphub.enums.SubscriptionPlan;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.UnauthorizedException;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Company management endpoints.
 *
 * Public:
 *   GET /api/companies/public/{subdomain} — minimal company info (branding)
 *
 * Owner:
 *   GET    /api/companies/me       — own company details
 *   PATCH  /api/companies/me       — update own company profile
 *
 * SUPER_ADMIN:
 *   POST   /api/companies/admin    — register a company on behalf of owner
 *   GET    /api/companies          — list all companies
 *   GET    /api/companies/{id}     — get any company by ID
 *   PATCH  /api/companies/{id}/plan    — change subscription plan
 *   PATCH  /api/companies/{id}/status  — change company status
 *   DELETE /api/companies/{id}     — deactivate company
 *
 * Self-registration (POST /api/auth/register) is in AuthController —
 * that path creates both User and Company in one transaction.
 */
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Tenant management — company registration, settings, and administration")
public class CompanyController {

    private final CompanyService companyService;
    private final SecurityUtil   securityUtil;

    // ── Public ────────────────────────────────────────────────────

    @GetMapping("/public/{subdomain}")
    @Operation(summary = "Get public company info by subdomain — no auth required")
    public ResponseEntity<ApiResponse<CompanyPublicResponse>> getPublic(
            @PathVariable String subdomain) {
        return ResponseEntity.ok(ApiResponse.success(
            companyService.getBySubdomain(subdomain)));
    }

    // ── Company owner ─────────────────────────────────────────────

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Get own company details")
    public ResponseEntity<ApiResponse<CompanyResponse>> getMyCompany() {
        Long companyId = securityUtil.getCurrentCompanyId();
        if (companyId == null) {
            throw new UnauthorizedException("No company associated with this account");
        }
        return ResponseEntity.ok(ApiResponse.success(companyService.getById(companyId)));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('COMPANY_OWNER')")
    @Operation(summary = "Update own company profile — COMPANY_OWNER only")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateMyCompany(
            @Valid @RequestBody UpdateCompanyRequest request) {
        Long companyId = securityUtil.getCurrentCompanyId();
        if (companyId == null) {
            throw new UnauthorizedException("No company associated with this account");
        }
        return ResponseEntity.ok(ApiResponse.success(
            "Company profile updated",
            companyService.update(companyId, request)));
    }

    // ── SUPER_ADMIN ───────────────────────────────────────────────

    @PostMapping("/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Register a company directly — SUPER_ADMIN only. No email verification required.")
    public ResponseEntity<ApiResponse<CompanyResponse>> registerByAdmin(
            @Valid @RequestBody RegisterCompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Company registered and trial started",
                companyService.registerByAdmin(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "List all companies — SUPER_ADMIN / SYSTEM_ADMIN only")
    public ResponseEntity<ApiResponse<Page<CompanyResponse>>> listAll(
            @RequestParam(required = false) CompanyStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            companyService.listAll(status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SYSTEM_ADMIN')")
    @Operation(summary = "Get any company by ID — SUPER_ADMIN / SYSTEM_ADMIN only")
    public ResponseEntity<ApiResponse<CompanyResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(companyService.getById(id)));
    }

    @PatchMapping("/{id}/plan")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Change company subscription plan — SUPER_ADMIN only")
    public ResponseEntity<ApiResponse<CompanyResponse>> changePlan(
            @PathVariable Long id,
            @RequestParam SubscriptionPlan plan) {
        return ResponseEntity.ok(ApiResponse.success(
            "Subscription plan updated",
            companyService.changePlan(id, plan)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Change company status — SUPER_ADMIN only")
    public ResponseEntity<ApiResponse<CompanyResponse>> changeStatus(
            @PathVariable Long id,
            @RequestParam CompanyStatus status) {
        if (status == CompanyStatus.DEACTIVATED) {
            throw new BadRequestException(
                "Use DELETE /api/companies/{id} to deactivate a company");
        }
        return ResponseEntity.ok(ApiResponse.success(
            "Company status updated",
            companyService.changeStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Deactivate a company — SUPER_ADMIN only. Soft-deletes company and owner.")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        companyService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Company deactivated successfully"));
    }
}
