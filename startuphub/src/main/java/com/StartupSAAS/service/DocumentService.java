package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.NotificationRequestDTO;
import com.StartupSAAS.dto.response.DocumentResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    DocumentResponseDTO upload(Long serviceRequestId, Long uploadedById,
                               Long companyId, String label,
                               String notes, MultipartFile file);
    List<DocumentResponseDTO> getByServiceRequest(Long serviceRequestId);
    List<DocumentResponseDTO> getByCompany(Long companyId);
    DocumentResponseDTO getById(Long id);
    void delete(Long id);
}
