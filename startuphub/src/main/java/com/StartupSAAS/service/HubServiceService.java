package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.HubServiceRequestDTO;
import com.StartupSAAS.dto.response.HubServiceResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HubServiceService {

    HubServiceResponseDTO create(HubServiceRequestDTO dto, MultipartFile icon);
    List<HubServiceResponseDTO> getAll();
    HubServiceResponseDTO getById(Long id);
    List<HubServiceResponseDTO> getByCompany(Long companyId);
    List<HubServiceResponseDTO> getActiveByCompany(Long companyId);
    HubServiceResponseDTO update(Long id, HubServiceRequestDTO dto, MultipartFile icon);
    void delete(Long id);
}
