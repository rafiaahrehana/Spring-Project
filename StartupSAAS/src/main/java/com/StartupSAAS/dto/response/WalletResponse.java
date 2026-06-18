package com.StartupSAAS.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletResponse {
    private Long id;
    private Double balance;
    private Long companyId;
    private String companyName;
}