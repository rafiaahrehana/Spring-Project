package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.HubServiceMapper;
import com.StartupSAAS.dto.request.HubServiceRequestDTO;
import com.StartupSAAS.dto.response.HubServiceResponseDTO;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.HubService;
import com.StartupSAAS.entity.WorkflowTemplate;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.HubServiceRepository;
import com.StartupSAAS.repository.WorkflowTemplateRepository;
import com.StartupSAAS.service.HubServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HubServiceServiceImpl implements HubServiceService {

    private final HubServiceRepository hubServiceRepository;
    private final CompanyRepository companyRepository;
    private final WorkflowTemplateRepository workflowTemplateRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Transactional
    @Override
    public HubServiceResponseDTO create(HubServiceRequestDTO dto, MultipartFile icon) {

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + dto.getCompanyId()));

        HubService service = new HubService();
        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setPrice(dto.getPrice());
        service.setPriceType(dto.getPriceType());
        service.setEstimatedDays(dto.getEstimatedDays());
        service.setDefaultPriority(dto.getDefaultPriority());
        service.setCompany(company);
        service.setActive(true);

        if (dto.getWorkflowTemplateId() != null) {
            WorkflowTemplate wt = workflowTemplateRepository
                    .findById(dto.getWorkflowTemplateId())
                    .orElseThrow(() -> new RuntimeException(
                            "WorkflowTemplate not found with id: " + dto.getWorkflowTemplateId()));
            service.setWorkflowTemplate(wt);
        }

        if (icon != null && !icon.isEmpty()) {
            service.setIconUrl(uploadFile(icon, dto.getName(), "service-icons"));
        }

        HubService saved = hubServiceRepository.save(service);
        return HubServiceMapper.toDTO(
                hubServiceRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HubServiceResponseDTO> getAll() {
        return hubServiceRepository.findAllWithDetails()
                .stream().map(HubServiceMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HubServiceResponseDTO getById(Long id) {
        return HubServiceMapper.toDTO(
                hubServiceRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Service not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HubServiceResponseDTO> getByCompany(Long companyId) {
        return hubServiceRepository.findByCompanyId(companyId)
                .stream().map(HubServiceMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HubServiceResponseDTO> getActiveByCompany(Long companyId) {
        return hubServiceRepository.findByCompanyIdAndActiveTrue(companyId)
                .stream().map(HubServiceMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public HubServiceResponseDTO update(Long id, HubServiceRequestDTO dto, MultipartFile icon) {

        HubService service = hubServiceRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "Service not found with id: " + id));

        if (dto.getName() != null)           service.setName(dto.getName());
        if (dto.getDescription() != null)    service.setDescription(dto.getDescription());
        if (dto.getPrice() != null)          service.setPrice(dto.getPrice());
        if (dto.getPriceType() != null)      service.setPriceType(dto.getPriceType());
        if (dto.getEstimatedDays() != null)  service.setEstimatedDays(dto.getEstimatedDays());
        if (dto.getDefaultPriority() != null) service.setDefaultPriority(dto.getDefaultPriority());

        if (dto.getWorkflowTemplateId() != null) {
            WorkflowTemplate wt = workflowTemplateRepository
                    .findById(dto.getWorkflowTemplateId())
                    .orElseThrow(() -> new RuntimeException("WorkflowTemplate not found"));
            service.setWorkflowTemplate(wt);
        }

        if (icon != null && !icon.isEmpty()) {
            service.setIconUrl(uploadFile(icon, service.getName(), "service-icons"));
        }

        HubService saved = hubServiceRepository.save(service);
        return HubServiceMapper.toDTO(
                hubServiceRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    public void delete(Long id) {
        hubServiceRepository.deleteById(id);
    }

    private String uploadFile(MultipartFile file, String name, String folder) {
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
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}
