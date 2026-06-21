package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.SubscriptionPlan;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ActivateCompanyRequest {
    private SubscriptionPlan plan;
    private LocalDate startDate;
    private LocalDate endDate;
}