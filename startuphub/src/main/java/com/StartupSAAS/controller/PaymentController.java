package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.PaymentRequestDTO;
import com.StartupSAAS.dto.response.PaymentResponseDTO;
import com.StartupSAAS.enums.PaymentStatus;
import com.StartupSAAS.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> create(@RequestBody PaymentRequestDTO dto) {
        return new ResponseEntity<>(paymentService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAll() {
        List<PaymentResponseDTO> list = paymentService.getAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public PaymentResponseDTO getById(@PathVariable Long id) {
        return paymentService.getById(id);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<PaymentResponseDTO>> getByCompany(
            @PathVariable Long companyId) {
        List<PaymentResponseDTO> list = paymentService.getByCompany(companyId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<PaymentResponseDTO>> getByClient(
            @PathVariable Long clientId) {
        List<PaymentResponseDTO> list = paymentService.getByClient(clientId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<PaymentResponseDTO>> getByInvoice(
            @PathVariable Long invoiceId) {
        List<PaymentResponseDTO> list = paymentService.getByInvoice(invoiceId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}/status")
    public PaymentResponseDTO updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        PaymentStatus ps;
        try { ps = PaymentStatus.valueOf(status.toUpperCase()); }
        catch (IllegalArgumentException e) { throw new RuntimeException("Invalid status: " + status); }
        return paymentService.updateStatus(id, ps);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
