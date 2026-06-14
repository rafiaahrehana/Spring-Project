package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    // Request DTO -> Entity
    public Company toEntity(CompanyRequest request){
        return Company.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .subdomain(request.getSubdomain())
                .ownerId(request.getOwnerId())
                .role(request.getRole())
                .logo(request.getLogo())
                .website(request.getWebsite())
                .build();
    }

    // Entity -> Response DTO
    public CompanyResponse toDTO(Company company){
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .email(company.getEmail())
                .phone(company.getPhone())
                .address(company.getAddress())
                .subdomain(company.getSubdomain())
                .ownerId(company.getOwnerId())
                .role(company.getRole())
                .logo(company.getLogo())
                .website(company.getWebsite())
                .build();
    }
}
