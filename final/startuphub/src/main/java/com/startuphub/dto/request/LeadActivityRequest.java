package com.startuphub.dto.request;

import com.startuphub.enums.LeadActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record LeadActivityRequest(

    @NotNull(message = "Activity type is required")
    LeadActivityType activityType,

    @NotBlank(message = "Subject is required")
    @Size(max = 255)
    String subject,

    String description,

    LocalDateTime activityAt,

    LocalDateTime nextFollowUp
) {}
