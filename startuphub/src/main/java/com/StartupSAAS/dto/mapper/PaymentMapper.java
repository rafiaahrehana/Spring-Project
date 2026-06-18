package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.PaymentResponseDTO;
import com.StartupSAAS.entity.Client;
import com.StartupSAAS.entity.Payment;

public class PaymentMapper {

    public static PaymentResponseDTO toDTO(Payment payment) {

        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setPaymentType(payment.getPaymentType());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaidAt(payment.getPaidAt());
        dto.setNotes(payment.getNotes());
        dto.setCreatedAt(payment.getCreatedAt());

        if (payment.getInvoice() != null) {
            dto.setInvoiceId(payment.getInvoice().getId());
            dto.setInvoiceNumber(payment.getInvoice().getInvoiceNumber());
        }

        Client client = payment.getClient();
        if (client != null) {
            dto.setClientId(client.getId());
            if (client.getUser() != null) {
                dto.setClientFirstName(client.getUser().getFirstName());
                dto.setClientLastName(client.getUser().getLastName());
            }
        }

        if (payment.getCompany() != null) {
            dto.setCompanyId(payment.getCompany().getId());
            dto.setCompanyName(payment.getCompany().getName());
        }

        return dto;
    }
}
