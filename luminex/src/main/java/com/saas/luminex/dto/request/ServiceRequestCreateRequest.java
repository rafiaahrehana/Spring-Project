package com.saas.luminex.dto.request;

import com.saas.luminex.enums.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceRequestCreateRequest {

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    private Priority priority = Priority.NORMAL;

    private String clientNotes;
}
