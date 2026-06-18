package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.CompanyMapper;
import com.StartupSAAS.dto.request.CompanyRequestDTO;
import com.StartupSAAS.dto.response.CompanyResponseDTO;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.enums.CompanyStatus;
import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Transactional
    @Override
    public CompanyResponseDTO create(CompanyRequestDTO dto, MultipartFile logo) {

        if (companyRepository.existsBySubdomain(dto.getSubdomain())) {
            throw new RuntimeException("Subdomain already taken: " + dto.getSubdomain());
        }

        Company company = new Company();
        company.setName(dto.getName());
        company.setSubdomain(dto.getSubdomain().toLowerCase().trim());
        company.setAddress(dto.getAddress());
        company.setWebsite(dto.getWebsite());
        company.setPlan(dto.getPlan() != null ? dto.getPlan() : SubscriptionPlan.STARTER);
        company.setStatus(CompanyStatus.TRIAL);
        company.setPrimaryColor(dto.getPrimaryColor() != null ? dto.getPrimaryColor() : "#1B2A4A");
        company.setSecondaryColor(dto.getSecondaryColor() != null ? dto.getSecondaryColor() : "#C9A84C");

        // 14-day trial period
        company.setTrialEndsAt(LocalDateTime.now().plusDays(14));

        if (logo != null && !logo.isEmpty()) {
            company.setLogoUrl(uploadImage(logo, dto.getName(), "company"));
        }

        Company saved = companyRepository.save(company);
        return CompanyMapper.toDTO(
                companyRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getAll() {
        return companyRepository.findAllWithDetails()
                .stream().map(CompanyMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponseDTO getById(Long id) {
        return CompanyMapper.toDTO(
                companyRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Company not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getByStatus(CompanyStatus status) {
        return companyRepository.findByStatus(status)
                .stream().map(CompanyMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CompanyResponseDTO update(Long id, CompanyRequestDTO dto, MultipartFile logo) {

        Company company = companyRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + id));

        if (dto.getName() != null)          company.setName(dto.getName());
        if (dto.getAddress() != null)       company.setAddress(dto.getAddress());
        if (dto.getWebsite() != null)       company.setWebsite(dto.getWebsite());
        if (dto.getPlan() != null)          company.setPlan(dto.getPlan());
        if (dto.getPrimaryColor() != null)  company.setPrimaryColor(dto.getPrimaryColor());
        if (dto.getSecondaryColor() != null) company.setSecondaryColor(dto.getSecondaryColor());

        // Subdomain change: validate uniqueness
        if (dto.getSubdomain() != null
                && !dto.getSubdomain().equals(company.getSubdomain())) {
            if (companyRepository.existsBySubdomain(dto.getSubdomain())) {
                throw new RuntimeException("Subdomain already taken: " + dto.getSubdomain());
            }
            company.setSubdomain(dto.getSubdomain().toLowerCase().trim());
        }

        if (logo != null && !logo.isEmpty()) {
            company.setLogoUrl(uploadImage(logo, company.getName(), "company"));
        }

        Company saved = companyRepository.save(company);
        return CompanyMapper.toDTO(
                companyRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Transactional
    @Override
    public CompanyResponseDTO updateStatus(Long id, CompanyStatus status) {

        Company company = companyRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + id));

        company.setStatus(status);

        Company saved = companyRepository.save(company);
        return CompanyMapper.toDTO(
                companyRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    public void delete(Long id) {
        companyRepository.deleteById(id);
    }

    private String uploadImage(MultipartFile file, String name, String folder) {
        try {
            Path path = Paths.get(uploadDir, folder);
            if (!Files.exists(path)) Files.createDirectories(path);

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains("."))
                ext = original.substring(original.lastIndexOf("."));

            String fileName = name.trim().replaceAll("\\s+", "_")
                    + "_" + UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }
}
