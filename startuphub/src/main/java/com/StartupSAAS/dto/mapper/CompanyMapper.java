package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.CompanyResponseDTO;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;

public class CompanyMapper {

    public static CompanyResponseDTO toDTO(Company company) {

        CompanyResponseDTO dto = new CompanyResponseDTO();

        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setSubdomain(company.getSubdomain());
        dto.setPlan(company.getPlan());
        dto.setStatus(company.getStatus());
        dto.setLogoUrl(company.getLogoUrl());
        dto.setPrimaryColor(company.getPrimaryColor());
        dto.setSecondaryColor(company.getSecondaryColor());
        dto.setAddress(company.getAddress());
        dto.setWebsite(company.getWebsite());
        dto.setTrialEndsAt(company.getTrialEndsAt());
        dto.setCreatedAt(company.getCreatedAt());

        // Flatten owner User fields
        User owner = company.getOwner();
        if (owner != null) {
            dto.setOwnerId(owner.getId());
            dto.setOwnerName(owner.getFirstName() + " " + owner.getLastName());
            dto.setOwnerEmail(owner.getEmail());
        }

        return dto;
    }
}
