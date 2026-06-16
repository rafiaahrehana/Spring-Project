package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.CompanyMapper;
import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.CompanyService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CompanyServiceimpl implements CompanyService {

  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final CompanyMapper companyMapper;
  private final PasswordEncoder passwordEncoder;

  @Value("${image.upload.dir}")
  private String uploadDir;

  @Override
  @Transactional
  public CompanyResponse createCompany(CompanyRequest request, MultipartFile logo) {
    if (companyRepository.existsBySubdomain(request.getSubdomain())) {
      throw new RuntimeException("Subdomain already exists");
    }

    // Check if email exists for the User
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new RuntimeException("User email already exists");
    }

    // 1. Create and save the Owner User
    User owner = companyMapper.toUser(request, passwordEncoder);
    owner = userRepository.save(owner);

    // 2. Create and save the Company
    Company company = companyMapper.toCompany(request);
    company.setUser(owner);

    if (logo != null && !logo.isEmpty()) {
      String fileName = uploadImage(logo, request.getName());
      company.setLogo(fileName);
    }

    Company saved = companyRepository.save(company);
    return companyMapper.toDTO(saved);
  }

  @Override
  public CompanyResponse getCompanyById(Long id) {
    Company company =
        companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found"));
    return companyMapper.toDTO(company);
  }

  @Override
  public List<CompanyResponse> getAllCompanies() {
    List<CompanyResponse> list =
        companyRepository.findAll().stream().map(companyMapper::toDTO).toList();
    if (list.isEmpty()) {
      throw new ResourceNotFoundException("No companies found");
    }
    return list;
  }

  @Override
  public Page<Company> searchCompanies(String query, Pageable pageable) {
    Page<Company> page = companyRepository.findByNameContainingIgnoreCase(query, pageable);
    if (page.isEmpty()) {
      throw new ResourceNotFoundException("No companies found matching query: " + query);
    }
    return page;
  }

  @Override
  public Page<Company> getCompaniesByPackage(SubscriptionPlan subscriptionPlan, Pageable pageable) {
    Page<Company> page = companyRepository.findBySubscriptionPlan(subscriptionPlan, pageable);
    if (page.isEmpty()) {
      throw new ResourceNotFoundException(
          "No companies found with package type: " + subscriptionPlan);
    }
    return page;
  }

  @Override
  public CompanyResponse updateCompany(Long id, CompanyRequest request) {
    Company company =
        companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found"));

    company.setName(request.getName());
    company.setEmail(request.getEmail());
    company.setPhone(request.getPhone());
    company.setSubdomain(request.getSubdomain());
    company.setLogo(request.getLogo());
    company.setWebsite(request.getWebsite());

    Company updated = companyRepository.save(company);
    return companyMapper.toDTO(updated);
  }

  @Override
  public void deleteCompany(Long id) {
    companyRepository.deleteById(id);
  } // before delete need validation

  private String uploadImage(MultipartFile file, String name) {
    try {
      Path path = Paths.get(uploadDir, "company");

      if (!Files.exists(path)) {
        Files.createDirectories(path);
      }

      String ext = "";
      String original = file.getOriginalFilename();

      if (original != null && original.contains(".")) {
        ext = original.substring(original.lastIndexOf("."));
      }

      String fileName = name.trim().replaceAll("\\s+", "_") + "_" + UUID.randomUUID() + ext;

      Files.copy(file.getInputStream(), path.resolve(fileName));

      return fileName;

    } catch (Exception e) {
      throw new RuntimeException("Logo upload failed");
    }
  }
}
