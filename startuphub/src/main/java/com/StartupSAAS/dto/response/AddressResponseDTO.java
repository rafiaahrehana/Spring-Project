package com.StartupSAAS.dto.response;

import lombok.Data;

@Data
public class AddressResponseDTO {

    private Long   id;
    private String street;

    // PostOffice
    private Long   postOfficeId;
    private String postOfficeName;
    private String postalCode;

    // PoliceStation
    private Long   policeStationId;
    private String policeStationName;

    // District
    private Long   districtId;
    private String districtName;

    // Division
    private Long   divisionId;
    private String divisionName;

    // Country
    private Long   countryId;
    private String countryName;
}
