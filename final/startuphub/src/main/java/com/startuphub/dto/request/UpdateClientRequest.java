package com.startuphub.dto.request;

import com.startuphub.enums.ClientStatus;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(

    @Size(max = 150)
    String clientCompanyName,

    @Size(max = 100)
    String industry,

    @Size(max = 255)
    String website,

    @Size(max = 50)
    String taxId,

    ClientStatus status,

    Long accountManagerId,

    Boolean portalAccessEnabled
) {}
