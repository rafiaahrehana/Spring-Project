package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.EmployeeRequestDTO;
import com.StartupSAAS.dto.response.EmployeeResponseDTO;
import com.StartupSAAS.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // POST /api/employees
    // multipart: "employee" (JSON part) + "image" (file, optional)
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> create(
            @RequestPart("employee") EmployeeRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return new ResponseEntity<>(employeeService.create(dto, image), HttpStatus.CREATED);
    }

    // GET /api/employees
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAll() {
        List<EmployeeResponseDTO> list = employeeService.getAll();
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    // GET /api/employees/1
    @GetMapping("/{id}")
    public EmployeeResponseDTO getById(@PathVariable Long id) {
        return employeeService.getById(id);
    }

    // GET /api/employees/company/3
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<EmployeeResponseDTO>> getByCompany(
            @PathVariable Long companyId) {
        List<EmployeeResponseDTO> list = employeeService.getByCompany(companyId);
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    // PUT /api/employees/1
    @PutMapping("/{id}")
    public EmployeeResponseDTO update(
            @PathVariable Long id,
            @RequestPart("employee") EmployeeRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return employeeService.update(id, dto, image);
    }

    // DELETE /api/employees/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}
