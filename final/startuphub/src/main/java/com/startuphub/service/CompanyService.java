package com.startuphub.service;

import com.startuphub.dto.request.RegisterCompanyRequest;
import com.startuphub.dto.request.UpdateCompanyRequest;
import com.startuphub.dto.response.CompanyPublicResponse;
import com.startuphub.dto.response.CompanyResponse;
import com.startuphub.enums.CompanyStatus;
import com.startuphub.enums.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {

    /** SUPER_ADMIN: register a company on behalf of an owner */
    CompanyResponse registerByAdmin(RegisterCompanyRequest request);

    /** Owner / SUPER_ADMIN: get full company details */
    CompanyResponse getById(Long companyId);

    /** Public: get minimal company info by subdomain */
    CompanyPublicResponse getBySubdomain(String subdomain);

    /** Owner: update branding and contact fields */
    CompanyResponse update(Long companyId, UpdateCompanyRequest request);

    /** SUPER_ADMIN: list all companies with optional status filter */
    Page<CompanyResponse> listAll(CompanyStatus status, Pageable pageable);

    /** SUPER_ADMIN: change a company's subscription plan */
    CompanyResponse changePlan(Long companyId, SubscriptionPlan newPlan);

    /** SUPER_ADMIN: suspend or reactivate a company */
    CompanyResponse changeStatus(Long companyId, CompanyStatus newStatus);

    /** SUPER_ADMIN: permanently deactivate (soft delete) */
    void deactivate(Long companyId);
}
