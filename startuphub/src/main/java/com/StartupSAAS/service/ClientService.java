package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.ClientRequestDTO;
import com.StartupSAAS.dto.response.ClientResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClientService {

    ClientResponseDTO create(ClientRequestDTO dto, MultipartFile image);
    List<ClientResponseDTO> getAll();
    ClientResponseDTO getById(Long id);
    List<ClientResponseDTO> getByCompany(Long companyId);
    ClientResponseDTO update(Long id, ClientRequestDTO dto, MultipartFile image);
    void delete(Long id);
}
