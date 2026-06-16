package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.enums.Role;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
  EmployeeResponse saveEmployee(Long companyId, EmployeeRequest request, MultipartFile image);

  EmployeeResponse getEmployeeById(Long id);

  List<EmployeeResponse> getAllEmployees();

  List<EmployeeResponse> getEmployeesByRole(Role role);

  List<EmployeeResponse> getEmployeesByDesignation(Designation designation);

  void deleteEmployee(Long id);
}
