package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.InvoiceRequestDTO;
import com.StartupSAAS.dto.response.InvoiceResponseDTO;
import com.StartupSAAS.enums.InvoiceStatus;
import com.StartupSAAS.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceResponseDTO> create(@RequestBody InvoiceRequestDTO dto) {
        return new ResponseEntity<>(invoiceService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponseDTO>> getAll() {
        List<InvoiceResponseDTO> list = invoiceService.getAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public InvoiceResponseDTO getById(@PathVariable Long id) {
        return invoiceService.getById(id);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<InvoiceResponseDTO>> getByCompany(
            @PathVariable Long companyId) {
        List<InvoiceResponseDTO> list = invoiceService.getByCompany(companyId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<InvoiceResponseDTO>> getByClient(
            @PathVariable Long clientId) {
        List<InvoiceResponseDTO> list = invoiceService.getByClient(clientId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<List<InvoiceResponseDTO>> getByCompanyAndStatus(
            @PathVariable Long companyId, @PathVariable String status) {
        InvoiceStatus is;
        try { is = InvoiceStatus.valueOf(status.toUpperCase()); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().build(); }
        List<InvoiceResponseDTO> list = invoiceService.getByCompanyAndStatus(companyId, is);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}/status")
    public InvoiceResponseDTO updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        InvoiceStatus is;
        try { is = InvoiceStatus.valueOf(status.toUpperCase()); }
        catch (IllegalArgumentException e) { throw new RuntimeException("Invalid status: " + status); }
        return invoiceService.updateStatus(id, is);
    }

    @PutMapping("/{id}")
    public InvoiceResponseDTO update(
            @PathVariable Long id, @RequestBody InvoiceRequestDTO dto) {
        return invoiceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
