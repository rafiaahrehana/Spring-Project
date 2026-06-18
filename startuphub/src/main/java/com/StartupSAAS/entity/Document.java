package com.StartupSAAS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Document extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    private String fileUrl;

    private String fileType;   // pdf, jpg, png, docx

    private Long fileSizeBytes;

    // Label / category — e.g. "NID", "TIN Certificate", "Company Logo"
    private String label;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Link to service request this document belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id")
    private ServiceRequest serviceRequest;

    // Who uploaded it
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;

    // Which company it belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
