package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.UserRequestDTO;
import com.StartupSAAS.dto.response.UserResponseDTO;
import com.StartupSAAS.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserResponseDTO create(UserRequestDTO dto, MultipartFile profilePicture);
    List<UserResponseDTO> getAll();
    UserResponseDTO getById(Long id);
    List<UserResponseDTO> getByCompany(Long companyId);
    List<UserResponseDTO> getByCompanyAndRole(Long companyId, Role role);
    UserResponseDTO update(Long id, UserRequestDTO dto, MultipartFile profilePicture);
    void delete(Long id);
}
