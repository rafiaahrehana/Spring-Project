package com.saas.luminex.service;

import com.saas.luminex.dto.request.PaymentRequest;
import com.saas.luminex.dto.response.ApiResponse;
import com.saas.luminex.entity.Payment;
import com.saas.luminex.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface PaymentService {
    Payment createPayment(PaymentRequest request);
    Payment getPaymentById(Long id);
    Page<Payment> getAllPayments(Pageable pageable);
    Page<Payment> getMyPayments(Pageable pageable);
    Payment updatePaymentStatus(Long id, PaymentStatus status);
    BigDecimal getTotalRevenue();
}
