package com.startuphub.service;

import com.startuphub.dto.request.CreateInvoiceRequest;
import com.startuphub.dto.request.RecordPaymentRequest;
import com.startuphub.dto.response.InvoiceResponse;
import com.startuphub.dto.response.PaymentResponse;
import com.startuphub.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {

    InvoiceResponse create(CreateInvoiceRequest request);

    InvoiceResponse getById(Long id);

    Page<InvoiceResponse> listAll(InvoiceStatus status, Pageable pageable);

    Page<InvoiceResponse> listForClient(Long clientId, Pageable pageable);

    InvoiceResponse issue(Long id);

    InvoiceResponse cancel(Long id);

    PaymentResponse recordPayment(Long invoiceId, RecordPaymentRequest request);

    Page<PaymentResponse> getPayments(Long invoiceId, Pageable pageable);
}
