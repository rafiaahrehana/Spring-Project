package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.PriceType;
import com.StartupSAAS.enums.Priority;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HubServiceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private Double price;
    private PriceType priceType;
    private Integer estimatedDays;
    private Priority defaultPriority;
    private Boolean active;
    private LocalDateTime createdAt;

    // Flattened company
    private Long companyId;
    private String companyName;

    // Flattened workflow template
    private Long workflowTemplateId;
    private String workflowTemplateName;
}
