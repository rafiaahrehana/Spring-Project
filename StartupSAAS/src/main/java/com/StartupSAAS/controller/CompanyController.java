package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.service.CompanyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/company/")
public class CompanyController {

  private final CompanyService companyService;

  // Create Company
  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<CompanyResponse> create(
      @RequestPart("data") CompanyRequest request,
      @RequestPart(value = "logo", required = false) MultipartFile logo) {
    return new ResponseEntity<>(companyService.createCompany(request, logo), HttpStatus.CREATED);
  }

  // Get all companies
  @GetMapping
  public ResponseEntity<List<CompanyResponse>> getAll() {
    return ResponseEntity.ok(companyService.getAllCompanies());
  }

  // Get company by id
  @GetMapping("/{id}")
  public CompanyResponse getById(@PathVariable Long id) {
    return companyService.getCompanyById(id);
  }

  // Search company
  @GetMapping("/search")
  public ResponseEntity<Page<Company>> search(
      @RequestParam String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(companyService.searchCompanies(query, PageRequest.of(page, size)));
  }

  // Get company by package
  @GetMapping("/package/{subscriptionPlan}")
  public ResponseEntity<Page<Company>> getByPackage(
      @PathVariable SubscriptionPlan subscriptionPlan,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(
        companyService.getCompaniesByPackage(subscriptionPlan, PageRequest.of(page, size)));
  }

  // Update company
  @PutMapping("/{id}")
  public ResponseEntity<CompanyResponse> update(
      @PathVariable Long id, @RequestBody CompanyRequest request) {
    return ResponseEntity.ok(companyService.updateCompany(id, request));
  }

  // Delete company
  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Long id) {
    companyService.deleteCompany(id);
    return ResponseEntity.ok("Deleted successfully");
  }
}
