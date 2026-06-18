package com.StartupSAAS.dto.response;

import lombok.Data;

@Data
public class WalletResponseDTO {
    private Long id;
    private Double balance;

    // Flattened client
    private Long clientId;
    private String clientFirstName;
    private String clientLastName;
    private String clientEmail;
}
