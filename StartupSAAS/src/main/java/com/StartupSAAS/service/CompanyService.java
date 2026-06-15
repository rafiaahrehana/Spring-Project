package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyService {
    CompanyResponse createCompany(CompanyRequest request);
    CompanyResponse getCompanyById(Long id);
    List<CompanyResponse> getAllCompanies();
    Page<Company> searchCompanies(String query, Pageable pageable);
    CompanyResponse updateCompany(Long id, CompanyRequest request);
    void deleteCompany(Long id);
}
