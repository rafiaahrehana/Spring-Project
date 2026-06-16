package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.enums.SubscriptionPlan;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface CompanyService {
  CompanyResponse createCompany(CompanyRequest request, MultipartFile logo);

  CompanyResponse getCompanyById(Long id);

  List<CompanyResponse> getAllCompanies();

  Page<Company> searchCompanies(String query, Pageable pageable);

  Page<Company> getCompaniesByPackage(SubscriptionPlan subscriptionPlan, Pageable pageable);

  CompanyResponse updateCompany(Long id, CompanyRequest request);

  void deleteCompany(Long id);
}
