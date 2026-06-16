package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.enums.Role;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
  EmployeeResponse createEmployee(Long companyId, EmployeeRequest request, MultipartFile image);

  // Get an employee by their ID
  EmployeeResponse getEmployeeById(Long id);

  // Get a list of all employees
  List<EmployeeResponse> getAllEmployees();

  // Get a list of employees by their Role
  List<EmployeeResponse> getEmployeesByRole(Role role);

  // Get a list of employees by their Designation
  List<EmployeeResponse> getEmployeesByDesignation(Designation designation);

  // Delete an employee by their ID
  void deleteEmployee(Long id);
}
