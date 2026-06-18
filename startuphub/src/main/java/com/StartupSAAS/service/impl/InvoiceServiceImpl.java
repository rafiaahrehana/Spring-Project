package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.InvoiceMapper;
import com.StartupSAAS.dto.request.InvoiceRequestDTO;
import com.StartupSAAS.dto.response.InvoiceResponseDTO;
import com.StartupSAAS.entity.*;
import com.StartupSAAS.enums.InvoiceStatus;
import com.StartupSAAS.repository.*;
import com.StartupSAAS.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ClientRepository clientRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    @Override
    public InvoiceResponseDTO create(InvoiceRequestDTO dto) {

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException(
                        "Client not found with id: " + dto.getClientId()));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + dto.getCompanyId()));

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setSubtotal(dto.getSubtotal());
        invoice.setDiscountAmount(dto.getDiscountAmount() != null ? dto.getDiscountAmount() : 0.0);
        invoice.setTaxAmount(dto.getTaxAmount() != null ? dto.getTaxAmount() : 0.0);
        invoice.setTotalAmount(dto.getTotalAmount());
        invoice.setPaidAmount(0.0);
        invoice.setIssueDate(dto.getIssueDate() != null ? dto.getIssueDate() : LocalDate.now());
        invoice.setDueDate(dto.getDueDate());
        invoice.setNotes(dto.getNotes());
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setClient(client);
        invoice.setCompany(company);

        if (dto.getServiceRequestId() != null) {
            ServiceRequest sr = serviceRequestRepository
                    .findById(dto.getServiceRequestId())
                    .orElseThrow(() -> new RuntimeException("ServiceRequest not found"));
            invoice.setServiceRequest(sr);
        }

        Invoice saved = invoiceRepository.save(invoice);
        return InvoiceMapper.toDTO(
                invoiceRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getAll() {
        return invoiceRepository.findAll()
                .stream().map(InvoiceMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getById(Long id) {
        return InvoiceMapper.toDTO(
                invoiceRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Invoice not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getByCompany(Long companyId) {
        return invoiceRepository.findByCompanyId(companyId)
                .stream().map(InvoiceMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getByClient(Long clientId) {
        return invoiceRepository.findByClientId(clientId)
                .stream().map(InvoiceMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getByCompanyAndStatus(
            Long companyId, InvoiceStatus status) {
        return invoiceRepository.findByCompanyIdAndStatus(companyId, status)
                .stream().map(InvoiceMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public InvoiceResponseDTO updateStatus(Long id, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "Invoice not found with id: " + id));
        invoice.setStatus(status);
        Invoice saved = invoiceRepository.save(invoice);
        return InvoiceMapper.toDTO(
                invoiceRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Transactional
    @Override
    public InvoiceResponseDTO update(Long id, InvoiceRequestDTO dto) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "Invoice not found with id: " + id));

        if (dto.getSubtotal() != null)        invoice.setSubtotal(dto.getSubtotal());
        if (dto.getDiscountAmount() != null)  invoice.setDiscountAmount(dto.getDiscountAmount());
        if (dto.getTaxAmount() != null)       invoice.setTaxAmount(dto.getTaxAmount());
        if (dto.getTotalAmount() != null)     invoice.setTotalAmount(dto.getTotalAmount());
        if (dto.getDueDate() != null)         invoice.setDueDate(dto.getDueDate());
        if (dto.getNotes() != null)           invoice.setNotes(dto.getNotes());

        Invoice saved = invoiceRepository.save(invoice);
        return InvoiceMapper.toDTO(
                invoiceRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    public void delete(Long id) {
        invoiceRepository.deleteById(id);
    }

    // Auto-generate invoice number: INV-2024-0001
    private String generateInvoiceNumber() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long count = invoiceRepository.count() + 1;
        return String.format("INV-%s-%04d", year, count);
    }
}
