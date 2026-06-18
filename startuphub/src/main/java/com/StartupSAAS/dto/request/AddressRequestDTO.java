package com.StartupSAAS.dto.request;

import lombok.Data;

@Data
public class AddressRequestDTO {
    private String street;
    private Long postOfficeId;  // selects full chain: PostOffice → PoliceStation → District → Division → Country
}
