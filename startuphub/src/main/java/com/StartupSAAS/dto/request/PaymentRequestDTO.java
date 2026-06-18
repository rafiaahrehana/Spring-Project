package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.PaymentMethod;
import com.StartupSAAS.enums.PaymentType;
import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentType paymentType;
    private String transactionId;
    private String notes;
    private Long invoiceId;
    private Long clientId;
    private Long companyId;
}
