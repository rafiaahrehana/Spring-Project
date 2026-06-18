package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.HubServiceResponseDTO;
import com.StartupSAAS.entity.HubService;

public class HubServiceMapper {

    public static HubServiceResponseDTO toDTO(HubService service) {

        HubServiceResponseDTO dto = new HubServiceResponseDTO();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setIconUrl(service.getIconUrl());
        dto.setPrice(service.getPrice());
        dto.setPriceType(service.getPriceType());
        dto.setEstimatedDays(service.getEstimatedDays());
        dto.setDefaultPriority(service.getDefaultPriority());
        dto.setActive(service.getActive());
        dto.setCreatedAt(service.getCreatedAt());

        if (service.getCompany() != null) {
            dto.setCompanyId(service.getCompany().getId());
            dto.setCompanyName(service.getCompany().getName());
        }

        if (service.getWorkflowTemplate() != null) {
            dto.setWorkflowTemplateId(service.getWorkflowTemplate().getId());
            dto.setWorkflowTemplateName(service.getWorkflowTemplate().getName());
        }

        return dto;
    }
}
