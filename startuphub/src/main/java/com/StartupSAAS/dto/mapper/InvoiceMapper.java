package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.InvoiceResponseDTO;
import com.StartupSAAS.entity.Client;
import com.StartupSAAS.entity.Invoice;

public class InvoiceMapper {

    public static InvoiceResponseDTO toDTO(Invoice invoice) {

        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setStatus(invoice.getStatus());
        dto.setSubtotal(invoice.getSubtotal());
        dto.setDiscountAmount(invoice.getDiscountAmount());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setPaidAmount(invoice.getPaidAmount());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setNotes(invoice.getNotes());
        dto.setCreatedAt(invoice.getCreatedAt());

        if (invoice.getServiceRequest() != null) {
            dto.setServiceRequestId(invoice.getServiceRequest().getId());
            dto.setServiceRequestTitle(invoice.getServiceRequest().getTitle());
        }

        Client client = invoice.getClient();
        if (client != null) {
            dto.setClientId(client.getId());
            if (client.getUser() != null) {
                dto.setClientFirstName(client.getUser().getFirstName());
                dto.setClientLastName(client.getUser().getLastName());
                dto.setClientEmail(client.getUser().getEmail());
            }
        }

        if (invoice.getCompany() != null) {
            dto.setCompanyId(invoice.getCompany().getId());
            dto.setCompanyName(invoice.getCompany().getName());
        }

        return dto;
    }
}
