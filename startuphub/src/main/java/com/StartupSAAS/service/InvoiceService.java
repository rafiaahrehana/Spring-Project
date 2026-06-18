package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.InvoiceRequestDTO;
import com.StartupSAAS.dto.response.InvoiceResponseDTO;
import com.StartupSAAS.enums.InvoiceStatus;

import java.util.List;

public interface InvoiceService {

    InvoiceResponseDTO create(InvoiceRequestDTO dto);
    List<InvoiceResponseDTO> getAll();
    InvoiceResponseDTO getById(Long id);
    List<InvoiceResponseDTO> getByCompany(Long companyId);
    List<InvoiceResponseDTO> getByClient(Long clientId);
    List<InvoiceResponseDTO> getByCompanyAndStatus(Long companyId, InvoiceStatus status);
    InvoiceResponseDTO updateStatus(Long id, InvoiceStatus status);
    InvoiceResponseDTO update(Long id, InvoiceRequestDTO dto);
    void delete(Long id);
}
