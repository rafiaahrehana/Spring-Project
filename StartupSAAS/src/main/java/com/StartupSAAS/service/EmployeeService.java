package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;

public interface EmployeeService {
    EmployeeResponse createEmployee(Long companyId, EmployeeRequest request);
}
