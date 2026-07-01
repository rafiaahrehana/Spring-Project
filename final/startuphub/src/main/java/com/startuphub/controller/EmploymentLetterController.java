package com.startuphub.controller;

import com.startuphub.dto.request.EmploymentLetterRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.EmploymentLetterResponse;
import com.startuphub.service.EmploymentLetterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/letters")
@RequiredArgsConstructor
@Tag(name = "Employment Letters", description = "HR letters creation and issuance")
public class EmploymentLetterController {

    private final EmploymentLetterService letterService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Create an employment letter")
    public ResponseEntity<ApiResponse<EmploymentLetterResponse>> create(
            @Valid @RequestBody EmploymentLetterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Letter created", letterService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "List all employment letters")
    public ResponseEntity<ApiResponse<Page<EmploymentLetterResponse>>> listAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            letterService.listAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "List letters for an employee")
    public ResponseEntity<ApiResponse<Page<EmploymentLetterResponse>>> listForEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            letterService.listForEmployee(employeeId,
                PageRequest.of(page, size, Sort.by("issueDate").descending()))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get letter by ID")
    public ResponseEntity<ApiResponse<EmploymentLetterResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(letterService.getById(id)));
    }

    @PatchMapping("/{id}/issue")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Issue an employment letter — locks content")
    public ResponseEntity<ApiResponse<EmploymentLetterResponse>> issue(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Letter issued", letterService.issue(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Delete an unissued letter")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        letterService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Letter deleted"));
    }
}
