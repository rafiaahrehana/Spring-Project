package com.startuphub.mapper;

import com.startuphub.dto.response.InvoiceResponse;
import com.startuphub.dto.response.PaymentResponse;
import com.startuphub.entity.Client;
import com.startuphub.entity.Invoice;
import com.startuphub.entity.Payment;
import com.startuphub.entity.ServiceRequest;
import com.startuphub.entity.User;

import java.util.List;

public final class InvoiceMapper {

    private InvoiceMapper() {}

    public static InvoiceResponse toResponse(Invoice inv, List<Payment> payments) {
        Client client = inv.getClient();
        User clientUser = client != null ? client.getUser() : null;
        ServiceRequest sr = inv.getServiceRequest();
        User createdBy = inv.getCreatedBy();
        List<PaymentResponse> paymentResponses = payments == null ? List.of()
            : payments.stream().map(InvoiceMapper::toPaymentResponse).toList();
        return new InvoiceResponse(
            inv.getId(),
            inv.getInvoiceNumber(),
            inv.getStatus(),
            inv.getType(),
            inv.getSubtotal(),
            inv.getTaxRate(),
            inv.getTaxAmount(),
            inv.getDiscountAmount(),
            inv.getTotalAmount(),
            inv.getPaidAmount(),
            inv.getOutstandingAmount(),
            inv.getDueDate(),
            inv.getIssuedAt(),
            inv.getPaidAt(),
            inv.getNotes(),
            client != null ? client.getId() : null,
            clientUser != null ? clientUser.getFullName() : null,
            sr != null ? sr.getId() : null,
            sr != null ? sr.getTitle() : null,
            createdBy != null ? createdBy.getId() : null,
            createdBy != null ? createdBy.getFullName() : null,
            paymentResponses,
            inv.getCreatedAt()
        );
    }

    public static PaymentResponse toPaymentResponse(Payment p) {
        User recordedBy = p.getRecordedBy();
        Invoice inv = p.getInvoice();
        return new PaymentResponse(
            p.getId(),
            p.getAmount(),
            p.getPaymentMethod(),
            p.getPaymentReference(),
            p.getPaidAt(),
            p.getNotes(),
            inv != null ? inv.getId() : null,
            inv != null ? inv.getInvoiceNumber() : null,
            recordedBy != null ? recordedBy.getId() : null,
            recordedBy != null ? recordedBy.getFullName() : null,
            p.getCreatedAt()
        );
    }
}
