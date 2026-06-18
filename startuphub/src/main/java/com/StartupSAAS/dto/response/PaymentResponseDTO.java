package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.PaymentMethod;
import com.StartupSAAS.enums.PaymentStatus;
import com.StartupSAAS.enums.PaymentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
    private Long id;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private PaymentType paymentType;
    private String transactionId;
    private LocalDateTime paidAt;
    private String notes;
    private LocalDateTime createdAt;

    // Flattened invoice
    private Long invoiceId;
    private String invoiceNumber;

    // Flattened client
    private Long clientId;
    private String clientFirstName;
    private String clientLastName;

    // Flattened company
    private Long companyId;
    private String companyName;
}
