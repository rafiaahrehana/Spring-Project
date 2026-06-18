package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.PaymentRequestDTO;
import com.StartupSAAS.dto.response.PaymentResponseDTO;
import com.StartupSAAS.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {

    PaymentResponseDTO create(PaymentRequestDTO dto);
    List<PaymentResponseDTO> getAll();
    PaymentResponseDTO getById(Long id);
    List<PaymentResponseDTO> getByCompany(Long companyId);
    List<PaymentResponseDTO> getByClient(Long clientId);
    List<PaymentResponseDTO> getByInvoice(Long invoiceId);
    List<PaymentResponseDTO> getByCompanyAndStatus(Long companyId, PaymentStatus status);
    PaymentResponseDTO updateStatus(Long id, PaymentStatus status);
    void delete(Long id);
}
