package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.PaymentMapper;
import com.StartupSAAS.dto.request.PaymentRequestDTO;
import com.StartupSAAS.dto.response.PaymentResponseDTO;
import com.StartupSAAS.entity.*;
import com.StartupSAAS.enums.InvoiceStatus;
import com.StartupSAAS.enums.PaymentStatus;
import com.StartupSAAS.repository.*;
import com.StartupSAAS.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    @Override
    public PaymentResponseDTO create(PaymentRequestDTO dto) {

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException(
                        "Client not found with id: " + dto.getClientId()));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + dto.getCompanyId()));

        Payment payment = new Payment();
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentType(dto.getPaymentType());
        payment.setTransactionId(dto.getTransactionId());
        payment.setNotes(dto.getNotes());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setClient(client);
        payment.setCompany(company);

        if (dto.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(dto.getInvoiceId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));
            payment.setInvoice(invoice);

            // Update invoice paid amount
            double newPaid = (invoice.getPaidAmount() != null
                    ? invoice.getPaidAmount() : 0.0) + dto.getAmount();
            invoice.setPaidAmount(newPaid);

            if (newPaid >= invoice.getTotalAmount()) {
                invoice.setStatus(InvoiceStatus.PAID);
            } else if (newPaid > 0) {
                invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
            }
            invoiceRepository.save(invoice);
        }

        // Mark as PAID immediately if transactionId provided
        if (dto.getTransactionId() != null && !dto.getTransactionId().isBlank()) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
        }

        Payment saved = paymentRepository.save(payment);
        return PaymentMapper.toDTO(
                paymentRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getAll() {
        return paymentRepository.findAll()
                .stream().map(PaymentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getById(Long id) {
        return PaymentMapper.toDTO(
                paymentRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Payment not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getByCompany(Long companyId) {
        return paymentRepository.findByCompanyId(companyId)
                .stream().map(PaymentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getByClient(Long clientId) {
        return paymentRepository.findByClientId(clientId)
                .stream().map(PaymentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId)
                .stream().map(PaymentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getByCompanyAndStatus(
            Long companyId, PaymentStatus status) {
        return paymentRepository.findByCompanyIdAndStatus(companyId, status)
                .stream().map(PaymentMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public PaymentResponseDTO updateStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found with id: " + id));
        payment.setStatus(status);
        if (status == PaymentStatus.PAID) {
            payment.setPaidAt(LocalDateTime.now());
        }
        Payment saved = paymentRepository.save(payment);
        return PaymentMapper.toDTO(
                paymentRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }
}
