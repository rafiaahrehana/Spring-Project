package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.CompanyStatus;
import com.StartupSAAS.enums.SubscriptionPlan;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyResponseDTO {

    private Long   id;
    private String name;
    private String subdomain;

    // Flattened owner info
    private Long   ownerId;
    private String ownerName;
    private String ownerEmail;

    private SubscriptionPlan plan;
    private CompanyStatus    status;
    private String           logoUrl;
    private String           primaryColor;
    private String           secondaryColor;
    private String           address;
    private String           website;
    private LocalDateTime    trialEndsAt;
    private LocalDateTime    createdAt;
}
