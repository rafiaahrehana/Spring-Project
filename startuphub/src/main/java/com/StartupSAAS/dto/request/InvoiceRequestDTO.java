package com.StartupSAAS.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InvoiceRequestDTO {
    private Double subtotal;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String notes;
    private Long serviceRequestId;
    private Long clientId;
    private Long companyId;
}
