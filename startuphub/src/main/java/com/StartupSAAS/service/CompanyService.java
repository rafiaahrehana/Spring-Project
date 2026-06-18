package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.CompanyRequestDTO;
import com.StartupSAAS.dto.response.CompanyResponseDTO;
import com.StartupSAAS.enums.CompanyStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CompanyService {

    CompanyResponseDTO create(CompanyRequestDTO dto, MultipartFile logo);
    List<CompanyResponseDTO> getAll();
    CompanyResponseDTO getById(Long id);
    List<CompanyResponseDTO> getByStatus(CompanyStatus status);
    CompanyResponseDTO update(Long id, CompanyRequestDTO dto, MultipartFile logo);
    CompanyResponseDTO updateStatus(Long id, CompanyStatus status);
    void delete(Long id);
}
