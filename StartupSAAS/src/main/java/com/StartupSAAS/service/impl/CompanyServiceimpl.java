package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.CompanyMapper;
import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CompanyServiceimpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    public CompanyResponse createCompany(CompanyRequest request) {
        if(companyRepository.existsBySubdomain(request.getSubdomain())){
            throw new RuntimeException("Subdomain already exists");
        }
        Company company = companyMapper.toEntity(request);
        Company saved = companyRepository.save(company);
        return companyMapper.toDTO(saved);
    }

    @Override
    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Company not found"));
        return companyMapper.toDTO(company);
    }

    @Override
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll().stream().map(companyMapper::toDTO).toList();
    }

    @Override
    public Page<Company> getActiveCompanies(Pageable pageable) {
        return companyRepository.findByIsActive(true, pageable);
    }

    @Override
    public Page<Company> searchCompanies(String query, Pageable pageable) {
            return companyRepository.findByNameContainingIgnoreCase(query, pageable);
    }

    @Override
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        Company company = companyRepository.findById(id).orElseThrow(
                                () -> new RuntimeException("Company not found"));

        company.setName(request.getName());
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setAddress(request.getAddress());
        company.setSubdomain(request.getSubdomain());
        company.setLogo(request.getLogo());
        company.setWebsite(request.getWebsite());

        Company updated = companyRepository.save(company);
        return companyMapper.toDTO(updated);
    }

    @Override
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    } //before delete need validation
}
