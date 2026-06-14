package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/company/")
public class CompanyController {

    private final CompanyService companyService;

    // Create Company
    @PostMapping
    public ResponseEntity<CompanyResponse> create(@RequestBody CompanyRequest request){
        return ResponseEntity.ok(companyService.createCompany(request));
    }

    // Get all companies
    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAll(){
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    // Get company by id
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    // Active companies with pagination
    @GetMapping("/active")
    public ResponseEntity<Page<Company>> activeCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(companyService.getActiveCompanies(PageRequest.of(page, size))
        );
    }

    // Search company
    @GetMapping("/search")
    public ResponseEntity<Page<Company>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(companyService.searchCompanies(query, PageRequest.of(page,size))
        );
    }
    // Update company
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> update(
            @PathVariable Long id,
            @RequestBody CompanyRequest request){
        return ResponseEntity.ok(companyService.updateCompany(id, request));
    }

    // Delete company
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        companyService.deleteCompany(id);
        return ResponseEntity.ok("Company deleted successfully");
    }
}
