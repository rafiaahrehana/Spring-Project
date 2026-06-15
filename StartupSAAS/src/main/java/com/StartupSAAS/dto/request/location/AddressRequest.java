package com.StartupSAAS.dto.request.location;

import lombok.Data;

@Data
public class AddressRequest {
    private Long id;
    private String houseNo;
    private String road;
    private Long postOfficeId;
}
