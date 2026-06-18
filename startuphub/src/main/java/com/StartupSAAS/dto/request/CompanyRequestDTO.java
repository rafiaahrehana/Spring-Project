package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.SubscriptionPlan;
import lombok.Data;

@Data
public class CompanyRequestDTO {
    private String name;
    private String subdomain;
    private String address;
    private String website;
    private String primaryColor;
    private String secondaryColor;
    private SubscriptionPlan plan;
}
