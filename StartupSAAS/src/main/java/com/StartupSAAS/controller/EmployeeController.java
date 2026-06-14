package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;


    @PostMapping("/{companyId}")
    public EmployeeResponse  create(@PathVariable Long companyId,
                                   @RequestBody EmployeeRequest request) {
        return employeeService.createEmployee(companyId, request);
    }
}
