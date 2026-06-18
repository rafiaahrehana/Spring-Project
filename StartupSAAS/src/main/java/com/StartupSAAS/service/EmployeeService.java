package com.StartupSAAS.service;

import com.StartupSAAS.approvalRequest.EmployeeSetupRequest;
import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.enums.Designation;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
  EmployeeResponse saveEmployee(Long companyId, EmployeeRequest request, MultipartFile image);
  EmployeeResponse getEmployeeById(Long id);
  EmployeeResponse updateEmployee(Long id, EmployeeRequest request, MultipartFile image);
  EmployeeResponse assignRoleAndDesignation(Long id, EmployeeSetupRequest request);
  List<EmployeeResponse> getAllEmployees();
  List<EmployeeResponse> getEmployeesByDesignation(Designation designation);
  void deleteEmployee(Long id);
}