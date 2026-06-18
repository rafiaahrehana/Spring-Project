package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.EmployeeRequestDTO;
import com.StartupSAAS.dto.response.EmployeeResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDTO create(EmployeeRequestDTO dto, MultipartFile image);
    List<EmployeeResponseDTO> getAll();
    EmployeeResponseDTO getById(Long id);
    List<EmployeeResponseDTO> getByCompany(Long companyId);
    EmployeeResponseDTO update(Long id, EmployeeRequestDTO dto, MultipartFile image);
    void delete(Long id);
}
