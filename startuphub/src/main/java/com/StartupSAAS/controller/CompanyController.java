package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.CompanyRequestDTO;
import com.StartupSAAS.dto.response.CompanyResponseDTO;
import com.StartupSAAS.enums.CompanyStatus;
import com.StartupSAAS.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // POST /api/companies
    // multipart: "company" (JSON part) + "logo" (file, optional)
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> create(
            @RequestPart("company") CompanyRequestDTO dto,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {
        return new ResponseEntity<>(companyService.create(dto, logo), HttpStatus.CREATED);
    }

    // GET /api/companies
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAll() {
        List<CompanyResponseDTO> list = companyService.getAll();
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    // GET /api/companies/1
    @GetMapping("/{id}")
    public CompanyResponseDTO getById(@PathVariable Long id) {
        return companyService.getById(id);
    }

    // GET /api/companies/status/ACTIVE
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CompanyResponseDTO>> getByStatus(
            @PathVariable String status) {
        CompanyStatus companyStatus;
        try {
            companyStatus = CompanyStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        List<CompanyResponseDTO> list = companyService.getByStatus(companyStatus);
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    // PUT /api/companies/1
    @PutMapping("/{id}")
    public CompanyResponseDTO update(
            @PathVariable Long id,
            @RequestPart("company") CompanyRequestDTO dto,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {
        return companyService.update(id, dto, logo);
    }

    // PATCH /api/companies/1/status
    // Body: { "status": "ACTIVE" }
    @PatchMapping("/{id}/status")
    public CompanyResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        CompanyStatus companyStatus;
        try {
            companyStatus = CompanyStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
        return companyService.updateStatus(id, companyStatus);
    }

    // DELETE /api/companies/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.ok("Company deleted successfully");
    }
}
