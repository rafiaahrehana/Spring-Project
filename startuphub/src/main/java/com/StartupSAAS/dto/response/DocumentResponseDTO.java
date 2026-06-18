package com.StartupSAAS.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentResponseDTO {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSizeBytes;
    private String label;
    private String notes;
    private LocalDateTime createdAt;

    // Flattened service request
    private Long serviceRequestId;
    private String serviceRequestTitle;

    // Flattened uploader
    private Long uploadedById;
    private String uploadedByName;

    // Flattened company
    private Long companyId;
    private String companyName;
}
