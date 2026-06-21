package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.ActivateCompanyRequest;
import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

  private final CompanyService companyService;

  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<CompanyResponse> create(
          @RequestPart("data") CompanyRequest request,
          @RequestPart(value = "logo", required = false) MultipartFile logo) {
    return new ResponseEntity<>(companyService.createCompany(request, logo), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<CompanyResponse>> getAll() {
    return ResponseEntity.ok(companyService.getAllCompanies());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CompanyResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(companyService.getCompanyById(id));
  }

  @GetMapping("/search")
  public ResponseEntity<Page<CompanyResponse>> search(
          @RequestParam String query,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(companyService.searchCompanies(query, PageRequest.of(page, size)));
  }

  @GetMapping("/package/{subscriptionPlan}")
  public ResponseEntity<Page<CompanyResponse>> getByPackage(
          @PathVariable SubscriptionPlan subscriptionPlan,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(companyService.getCompaniesByPackage(subscriptionPlan, PageRequest.of(page, size)));
  }

  @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
  public ResponseEntity<CompanyResponse> update(
          @PathVariable Long id,
          @RequestPart("data") CompanyRequest request,
          @RequestPart(value = "logo", required = false) MultipartFile logo) {
    return ResponseEntity.ok(companyService.updateCompany(id, request, logo));
  }
  @PatchMapping("/{id}/activate")
  public ResponseEntity<CompanyResponse> activate(
          @PathVariable Long id,
          @RequestBody ActivateCompanyRequest request) {
    return ResponseEntity.ok(companyService.activateCompany(id, request));
  }

  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<CompanyResponse> deactivate(@PathVariable Long id) {
    return ResponseEntity.ok(companyService.deactivateCompany(id));
  }
  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Long id) {
    companyService.deleteCompany(id);
    return ResponseEntity.ok("Deleted successfully");
  }
}