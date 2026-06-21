package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.EmployeeSetupRequest;
import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.service.EmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping(value = "/{companyId}", consumes = {"multipart/form-data"})
    public ResponseEntity<EmployeeResponse> create(
            @PathVariable Long companyId,
            @RequestPart("data") EmployeeRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return new ResponseEntity<>(employeeService.saveEmployee(companyId, request, image), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAll() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/designation/{designation}")
    public ResponseEntity<List<EmployeeResponse>> getByDesignation(@PathVariable Designation designation) {
        return ResponseEntity.ok(employeeService.getEmployeesByDesignation(designation));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable Long id,
            @RequestPart("data") EmployeeRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request, image));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<EmployeeResponse> assign(
            @PathVariable Long id,
            @RequestBody EmployeeSetupRequest request) {
        return ResponseEntity.ok(employeeService.assignRoleAndDesignation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}