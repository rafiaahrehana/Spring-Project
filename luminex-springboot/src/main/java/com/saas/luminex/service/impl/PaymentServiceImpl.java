package com.saas.luminex.service.impl;

import com.saas.luminex.dto.request.PaymentRequest;
import com.saas.luminex.entity.Payment;
import com.saas.luminex.entity.ServiceRequest;
import com.saas.luminex.entity.User;
import com.saas.luminex.enums.NotificationType;
import com.saas.luminex.enums.PaymentStatus;
import com.saas.luminex.exception.ResourceNotFoundException;
import com.saas.luminex.repository.PaymentRepository;
import com.saas.luminex.repository.ServiceRequestRepository;
import com.saas.luminex.service.NotificationService;
import com.saas.luminex.service.PaymentService;
import com.saas.luminex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public Payment createPayment(PaymentRequest dto) {
        User client = securityUtil.getCurrentUser();

        ServiceRequest serviceRequest = null;
        if (dto.getServiceRequestId() != null) {
            serviceRequest = serviceRequestRepository.findById(dto.getServiceRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("ServiceRequest", dto.getServiceRequestId()));
        }

        Payment payment = Payment.builder()
                .client(client)
                .serviceRequest(serviceRequest)
                .amount(dto.getAmount())
                .method(dto.getMethod())
                .transactionId(dto.getTransactionId())
                .description(dto.getDescription())
                .status(PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);

        notificationService.send(client,
                "Payment Received",
                "Your payment of ৳" + dto.getAmount() + " via " + dto.getMethod() + " is being processed.",
                NotificationType.PAYMENT_RECEIVED);

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Payment> getMyPayments(Pageable pageable) {
        User client = securityUtil.getCurrentUser();
        return paymentRepository.findByClient(client, pageable);
    }

    @Override
    @Transactional
    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return paymentRepository.sumTotalRevenue();
    }
}
