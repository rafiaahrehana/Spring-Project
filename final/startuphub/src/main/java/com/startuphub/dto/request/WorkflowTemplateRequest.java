package com.startuphub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkflowTemplateRequest(

    @NotBlank(message = "Workflow template name is required")
    @Size(max = 150)
    String name,

    String description
) {}
