package com.startuphub.service.impl;

import com.startuphub.dto.request.RegisterCompanyRequest;
import com.startuphub.dto.request.UpdateCompanyRequest;
import com.startuphub.dto.response.CompanyPublicResponse;
import com.startuphub.dto.response.CompanyResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.User;
import com.startuphub.enums.CompanyStatus;
import com.startuphub.enums.Role;
import com.startuphub.enums.SubscriptionPlan;
import com.startuphub.enums.TokenType;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.CompanyMapper;
import com.startuphub.repository.CompanyRepository;
import com.startuphub.repository.TokenRepository;
import com.startuphub.repository.UserRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.CompanyService;
import com.startuphub.service.EmailService;
import com.startuphub.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Company management service.
 *
 * Covers two entry paths:
 *
 *   Self-registration path (public):
 *     POST /api/auth/register → AuthServiceImpl.register()
 *     Creates both User and Company in one transaction.
 *     Company starts as PENDING_VERIFICATION.
 *     Email verification activates it → TRIAL.
 *
 *   Admin-registration path (SUPER_ADMIN only):
 *     POST /api/companies/admin → CompanyController → registerByAdmin()
 *     Creates both User and Company and immediately activates them.
 *     Trial starts immediately — no email verification step.
 *
 * Tenant isolation:
 *   Company IS the tenant — no company_id filter is applied to Company itself.
 *   All mutation operations verify ownership via SecurityUtil before modifying.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository            companyRepository;
    private final UserRepository               userRepository;
    private final TokenRepository              tokenRepository;
    private final PasswordEncoder              passwordEncoder;
    private final EmailService                 emailService;
    private final NotificationPreferenceService notificationPreferenceService;
    private final SecurityUtil                 securityUtil;

    @Value("${app.trial-days:14}")
    private int trialDays;

    // ── Admin registration ────────────────────────────────────────

    @Override
    @Transactional
    public CompanyResponse registerByAdmin(RegisterCompanyRequest request) {
        if (userRepository.existsByEmail(request.ownerEmail())) {
            throw new BadRequestException(
                "An account with this email already exists");
        }
        if (companyRepository.existsBySubdomain(request.subdomain())) {
            throw new BadRequestException("This subdomain is already taken");
        }

        User owner = User.builder()
            .firstName(request.ownerFirstName())
            .lastName(request.ownerLastName())
            .email(request.ownerEmail().toLowerCase().trim())
            .password(passwordEncoder.encode(request.ownerPassword()))
            .role(Role.COMPANY_OWNER)
            .active(true)
            .emailVerified(true)
            .build();
        userRepository.save(owner);

        // Admin registrations start the trial immediately
        Company company = Company.builder()
            .companyName(request.companyName())
            .subdomain(request.subdomain().toLowerCase().trim())
            .companyPhone(request.companyPhone())
            .subscriptionPlan(SubscriptionPlan.FREE)
            .status(CompanyStatus.TRIAL)
            .subscriptionStart(LocalDate.now())
            .subscriptionEnd(LocalDate.now().plusDays(trialDays))
            .owner(owner)
            .build();
        companyRepository.save(company);

        // Create default notification preferences for the owner
        notificationPreferenceService.createDefaultsForUser(owner.getId());

        emailService.sendWelcomeEmail(
            owner.getEmail(), owner.getFirstName(), company.getCompanyName());

        log.info("Company registered by admin: subdomain='{}' owner='{}'",
            company.getSubdomain(), owner.getEmail());

        return CompanyMapper.toResponse(company);
    }

    // ── Read ──────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getById(Long companyId) {
        return CompanyMapper.toResponse(findById(companyId));
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyPublicResponse getBySubdomain(String subdomain) {
        Company company = companyRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Company not found for subdomain: " + subdomain));
        return CompanyMapper.toPublicResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyResponse> listAll(CompanyStatus status, Pageable pageable) {
        Page<Company> page = (status != null)
            ? companyRepository.findByStatus(status, pageable)
            : companyRepository.findAll(pageable);
        return page.map(CompanyMapper::toResponse);
    }

    // ── Update ────────────────────────────────────────────────────

    @Override
    @Transactional
    public CompanyResponse update(Long companyId, UpdateCompanyRequest request) {
        Company company = findById(companyId);

        // Apply only the non-null fields from the request
        if (request.companyName()   != null) company.setCompanyName(request.companyName());
        if (request.companyPhone()  != null) company.setCompanyPhone(request.companyPhone());
        if (request.website()       != null) company.setWebsite(request.website());
        if (request.address()       != null) company.setAddress(request.address());
        if (request.logo()          != null) company.setLogo(request.logo());
        if (request.primaryColor()  != null) company.setPrimaryColor(request.primaryColor());
        if (request.secondaryColor()!= null) company.setSecondaryColor(request.secondaryColor());
        if (request.tagline()       != null) company.setTagline(request.tagline());

        log.info("Company updated: id={} by user={}", companyId,
            securityUtil.getCurrentUser().getEmail());

        return CompanyMapper.toResponse(company);
    }

    // ── Admin mutations ───────────────────────────────────────────

    @Override
    @Transactional
    public CompanyResponse changePlan(Long companyId, SubscriptionPlan newPlan) {
        Company company = findById(companyId);
        SubscriptionPlan old = company.getSubscriptionPlan();
        company.setSubscriptionPlan(newPlan);

        // Moving to a paid plan activates the company
        if (company.getStatus() == CompanyStatus.TRIAL
                || company.getStatus() == CompanyStatus.SUSPENDED) {
            company.setStatus(CompanyStatus.ACTIVE);
        }

        log.info("Plan changed: company={} {} → {}",
            companyId, old, newPlan);

        return CompanyMapper.toResponse(company);
    }

    @Override
    @Transactional
    public CompanyResponse changeStatus(Long companyId, CompanyStatus newStatus) {
        if (newStatus == CompanyStatus.DEACTIVATED) {
            throw new BadRequestException(
                "Use DELETE /api/companies/{id} to deactivate a company");
        }

        Company company = findById(companyId);
        CompanyStatus old = company.getStatus();
        company.setStatus(newStatus);

        log.info("Status changed: company={} {} → {}",
            companyId, old, newStatus);

        return CompanyMapper.toResponse(company);
    }

    @Override
    @Transactional
    public void deactivate(Long companyId) {
        Company company = findById(companyId);
        if (company.getStatus() == CompanyStatus.DEACTIVATED) {
            throw new BadRequestException("Company is already deactivated");
        }
        company.setStatus(CompanyStatus.DEACTIVATED);
        company.softDelete();

        // Deactivate the owner user as well
        User owner = company.getOwner();
        if (owner != null) {
            owner.setActive(false);
            owner.softDelete();
            userRepository.save(owner);
            // Revoke all tokens for the owner
            tokenRepository.revokeAllByUserIdAndType(owner.getId(), TokenType.REFRESH);
        }

        log.info("Company deactivated: id={} subdomain='{}'",
            companyId, company.getSubdomain());
    }

    // ── Private helpers ───────────────────────────────────────────

    private Company findById(Long id) {
        return companyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Company not found: " + id));
    }
}
