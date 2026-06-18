package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.DocumentResponseDTO;
import com.StartupSAAS.entity.Document;

public class DocumentMapper {

    public static DocumentResponseDTO toDTO(Document doc) {

        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setId(doc.getId());
        dto.setFileName(doc.getFileName());
        dto.setFileUrl(doc.getFileUrl());
        dto.setFileType(doc.getFileType());
        dto.setFileSizeBytes(doc.getFileSizeBytes());
        dto.setLabel(doc.getLabel());
        dto.setNotes(doc.getNotes());
        dto.setCreatedAt(doc.getCreatedAt());

        if (doc.getServiceRequest() != null) {
            dto.setServiceRequestId(doc.getServiceRequest().getId());
            dto.setServiceRequestTitle(doc.getServiceRequest().getTitle());
        }

        if (doc.getUploadedBy() != null) {
            dto.setUploadedById(doc.getUploadedBy().getId());
            dto.setUploadedByName(doc.getUploadedBy().getFirstName()
                    + " " + doc.getUploadedBy().getLastName());
        }

        if (doc.getCompany() != null) {
            dto.setCompanyId(doc.getCompany().getId());
            dto.setCompanyName(doc.getCompany().getName());
        }

        return dto;
    }
}
