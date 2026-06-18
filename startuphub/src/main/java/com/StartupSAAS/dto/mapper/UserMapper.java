package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.UserResponseDTO;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;

public class UserMapper {

    public static UserResponseDTO toDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setLanguagePref(user.getLanguagePref());
        dto.setEmailEnabled(user.getEmailEnabled());
        dto.setSmsEnabled(user.getSmsEnabled());
        dto.setIsActive(user.getIsActive());

        // Flatten Company fields
        Company company = user.getCompany();
        if (company != null) {
            dto.setCompanyId(company.getId());
            dto.setCompanyName(company.getName());
            dto.setSubdomain(company.getSubdomain());
        }

        return dto;
    }
}
