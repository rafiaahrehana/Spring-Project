package com.StartupSAAS.dto.response.location;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {

    private Long id;
    private String houseNo;
    private String road;
    private PostOfficeResponse postOffice;
}