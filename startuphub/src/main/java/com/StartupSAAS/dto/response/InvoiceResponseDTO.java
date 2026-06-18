package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.InvoiceStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InvoiceResponseDTO {
    private Long id;
    private String invoiceNumber;
    private InvoiceStatus status;
    private Double subtotal;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private Double paidAmount;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String notes;
    private LocalDateTime createdAt;

    // Flattened service request
    private Long serviceRequestId;
    private String serviceRequestTitle;

    // Flattened client
    private Long clientId;
    private String clientFirstName;
    private String clientLastName;
    private String clientEmail;

    // Flattened company
    private Long companyId;
    private String companyName;
}
