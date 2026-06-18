package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.PriceType;
import com.StartupSAAS.enums.Priority;
import lombok.Data;

@Data
public class HubServiceRequestDTO {
    private String name;
    private String description;
    private Double price;
    private PriceType priceType;
    private Integer estimatedDays;
    private Priority defaultPriority;
    private Long companyId;
    private Long workflowTemplateId;
}
