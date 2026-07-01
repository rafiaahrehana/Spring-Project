package com.startuphub.service.impl;

import com.startuphub.dto.request.CreateInvoiceRequest;
import com.startuphub.dto.request.CreateNotificationRequest;
import com.startuphub.dto.request.RecordPaymentRequest;
import com.startuphub.dto.response.InvoiceResponse;
import com.startuphub.dto.response.PaymentResponse;
import com.startuphub.entity.Client;
import com.startuphub.entity.Company;
import com.startuphub.entity.Invoice;
import com.startuphub.entity.Payment;
import com.startuphub.entity.ServiceRequest;
import com.startuphub.entity.User;
import com.startuphub.enums.InvoiceStatus;
import com.startuphub.enums.InvoiceType;
import com.startuphub.enums.NotificationType;
import com.startuphub.enums.WalletTransactionType;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.InvoiceMapper;
import com.startuphub.repository.ClientRepository;
import com.startuphub.repository.InvoiceRepository;
import com.startuphub.repository.PaymentRepository;
import com.startuphub.repository.ServiceRequestRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.InvoiceService;
import com.startuphub.service.NotificationService;
import com.startuphub.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository        invoiceRepository;
    private final PaymentRepository        paymentRepository;
    private final ClientRepository         clientRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final WalletService            walletService;
    private final NotificationService      notificationService;
    private final SecurityUtil             securityUtil;

    @Override
    @Transactional
    public InvoiceResponse create(CreateInvoiceRequest request) {
        Long companyId = requireCompanyId();
        User currentUser = securityUtil.getCurrentUser();

        Client client = clientRepository.findByIdAndCompanyId(request.clientId(), companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + request.clientId()));

        BigDecimal taxRate = request.taxRate() != null ? request.taxRate() : BigDecimal.ZERO;
        BigDecimal discount = request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO;
        BigDecimal subtotal = request.subtotal();
        BigDecimal taxAmount = subtotal.multiply(taxRate)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(taxAmount).subtract(discount);

        Invoice invoice = Invoice.builder()
            .invoiceNumber(generateInvoiceNumber(companyId))
            .status(InvoiceStatus.DRAFT)
            .type(request.type() != null ? request.type() : InvoiceType.FULL)
            .subtotal(subtotal)
            .taxRate(taxRate)
            .taxAmount(taxAmount)
            .discountAmount(discount)
            .totalAmount(total)
            .paidAmount(BigDecimal.ZERO)
            .dueDate(request.dueDate())
            .notes(request.notes())
            .company(companyRef(companyId))
            .client(client)
            .createdBy(currentUser)
            .build();

        if (request.serviceRequestId() != null) {
            ServiceRequest sr = serviceRequestRepository
                .findByIdAndCompanyId(request.serviceRequestId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Service request not found: " + request.serviceRequestId()));
            invoice.setServiceRequest(sr);
        }

        invoiceRepository.save(invoice);
        log.info("Invoice created: {} company={}", invoice.getInvoiceNumber(), companyId);
        return InvoiceMapper.toResponse(invoice, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getById(Long id) {
        Invoice invoice = findInTenant(id);
        List<Payment> payments = paymentRepository.findByInvoiceId(id);
        return InvoiceMapper.toResponse(invoice, payments);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> listAll(InvoiceStatus status, Pageable pageable) {
        Long companyId = requireCompanyId();
        Page<Invoice> page = status != null
            ? invoiceRepository.findByCompanyIdAndStatus(companyId, status, pageable)
            : invoiceRepository.findByCompanyId(companyId, pageable);
        return page.map(inv -> InvoiceMapper.toResponse(inv, List.of()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> listForClient(Long clientId, Pageable pageable) {
        Long companyId = requireCompanyId();
        return invoiceRepository.findByCompanyIdAndClientId(companyId, clientId, pageable)
            .map(inv -> InvoiceMapper.toResponse(inv, List.of()));
    }

    @Override
    @Transactional
    public InvoiceResponse issue(Long id) {
        Invoice invoice = findInTenant(id);
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT invoices can be issued");
        }
        invoice.setStatus(InvoiceStatus.ISSUED);
        invoice.setIssuedAt(LocalDateTime.now());

        // Notify the client
        Client client = invoice.getClient();
        if (client != null && client.getUser() != null) {
            notificationService.send(CreateNotificationRequest.of(
                NotificationType.INVOICE_GENERATED,
                "Invoice Issued",
                "Invoice " + invoice.getInvoiceNumber() + " has been issued for "
                    + invoice.getTotalAmount() + " BDT.",
                "/invoices/" + invoice.getId(),
                client.getUser().getId(),
                invoice.getCompany().getId()
            ));
        }

        log.info("Invoice issued: {} company={}", invoice.getInvoiceNumber(), invoice.getCompany().getId());
        return InvoiceMapper.toResponse(invoice, paymentRepository.findByInvoiceId(id));
    }

    @Override
    @Transactional
    public InvoiceResponse cancel(Long id) {
        Invoice invoice = findInTenant(id);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("Cannot cancel a fully paid invoice");
        }
        if (invoice.getStatus() == InvoiceStatus.CANCELLED
                || invoice.getStatus() == InvoiceStatus.VOIDED) {
            throw new BadRequestException("Invoice is already cancelled");
        }
        invoice.setStatus(InvoiceStatus.CANCELLED);
        log.info("Invoice cancelled: {}", invoice.getInvoiceNumber());
        return InvoiceMapper.toResponse(invoice, paymentRepository.findByInvoiceId(id));
    }

    @Override
    @Transactional
    public PaymentResponse recordPayment(Long invoiceId, RecordPaymentRequest request) {
        Long companyId = requireCompanyId();
        Invoice invoice = findInTenant(invoiceId);
        User currentUser = securityUtil.getCurrentUser();

        if (invoice.getStatus() == InvoiceStatus.CANCELLED
                || invoice.getStatus() == InvoiceStatus.VOIDED) {
            throw new BadRequestException("Cannot record payment on a cancelled invoice");
        }
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("Invoice is already fully paid");
        }

        BigDecimal outstanding = invoice.getOutstandingAmount();
        if (request.amount().compareTo(outstanding) > 0) {
            throw new BadRequestException("Payment amount " + request.amount()
                + " exceeds outstanding balance " + outstanding);
        }

        Payment payment = Payment.builder()
            .amount(request.amount())
            .paymentMethod(request.paymentMethod())
            .paymentReference(request.paymentReference())
            .paidAt(request.paidAt() != null ? request.paidAt() : LocalDateTime.now())
            .notes(request.notes())
            .invoice(invoice)
            .company(companyRef(companyId))
            .client(invoice.getClient())
            .recordedBy(currentUser)
            .build();
        paymentRepository.save(payment);

        // Update invoice paid amount and status
        BigDecimal newPaid = invoice.getPaidAmount().add(request.amount());
        invoice.setPaidAmount(newPaid);
        if (invoice.isFullyPaid()) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidAt(LocalDateTime.now());
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        // Credit payment to company wallet
        walletService.credit(companyId, request.amount(), WalletTransactionType.CREDIT,
            "Payment for " + invoice.getInvoiceNumber(), null);

        // Notify client
        Client client = invoice.getClient();
        if (client != null && client.getUser() != null) {
            notificationService.send(CreateNotificationRequest.of(
                NotificationType.PAYMENT_RECEIVED,
                "Payment Received",
                "Payment of " + request.amount() + " BDT received for "
                    + invoice.getInvoiceNumber() + ".",
                "/invoices/" + invoiceId,
                client.getUser().getId(),
                companyId
            ));
        }

        log.info("Payment recorded: invoice={} amount={} method={}",
            invoice.getInvoiceNumber(), request.amount(), request.paymentMethod());
        return InvoiceMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPayments(Long invoiceId, Pageable pageable) {
        findInTenant(invoiceId);
        return paymentRepository.findByCompanyId(requireCompanyId(), pageable)
            .map(InvoiceMapper::toPaymentResponse);
    }

    // ── Private helpers ───────────────────────────────────────────

    private Invoice findInTenant(Long id) {
        return invoiceRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }

    private Company companyRef(Long companyId) {
        Company c = new Company();
        c.setId(companyId);
        return c;
    }

    /**
     * Generates a per-company sequential invoice number.
     * Format: INV-{YYYY}-{sequence padded to 4 digits}
     * e.g. INV-2025-0001, INV-2025-0042
     */
    private String generateInvoiceNumber(Long companyId) {
        String year = String.valueOf(Year.now().getValue());
        String prefix = "INV-" + year + "-";
        return invoiceRepository.findMaxInvoiceNumberWithPrefix(companyId, prefix)
            .map(last -> {
                String seq = last.substring(last.lastIndexOf('-') + 1);
                int next = Integer.parseInt(seq) + 1;
                return prefix + String.format("%04d", next);
            })
            .orElse(prefix + "0001");
    }
}
