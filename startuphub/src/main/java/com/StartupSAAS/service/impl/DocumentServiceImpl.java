package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.DocumentMapper;
import com.StartupSAAS.dto.request.NotificationRequestDTO;
import com.StartupSAAS.dto.response.DocumentResponseDTO;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.Document;
import com.StartupSAAS.entity.ServiceRequest;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.DocumentRepository;
import com.StartupSAAS.repository.ServiceRequestRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.DocumentService;
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
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Transactional
    @Override
    public DocumentResponseDTO upload(Long serviceRequestId, Long uploadedById,
                                      Long companyId, String label,
                                      String notes, MultipartFile file) {

        ServiceRequest sr = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new RuntimeException(
                        "ServiceRequest not found with id: " + serviceRequestId));

        User uploader = userRepository.findById(uploadedById)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + uploadedById));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + companyId));

        // Save file to disk
        String fileUrl = saveFile(file, "documents");

        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setFileUrl(fileUrl);
        doc.setFileSizeBytes(file.getSize());
        doc.setLabel(label);
        doc.setNotes(notes);
        doc.setServiceRequest(sr);
        doc.setUploadedBy(uploader);
        doc.setCompany(company);

        // Detect file type from original name
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            doc.setFileType(original.substring(original.lastIndexOf(".") + 1).toLowerCase());
        }

        Document saved = documentRepository.save(doc);
        return DocumentMapper.toDTO(
                documentRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getByServiceRequest(Long serviceRequestId) {
        return documentRepository.findByServiceRequestId(serviceRequestId)
                .stream().map(DocumentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getByCompany(Long companyId) {
        return documentRepository.findByCompanyId(companyId)
                .stream().map(DocumentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponseDTO getById(Long id) {
        return DocumentMapper.toDTO(
                documentRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Document not found with id: " + id)));
    }

    @Override
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    private String saveFile(MultipartFile file, String folder) {
        try {
            Path path = Paths.get(uploadDir, folder);
            if (!Files.exists(path)) Files.createDirectories(path);
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains("."))
                ext = original.substring(original.lastIndexOf("."));
            String fileName = UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), path.resolve(fileName));
            return folder + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}
