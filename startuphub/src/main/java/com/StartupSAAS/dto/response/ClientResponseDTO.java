package com.StartupSAAS.dto.response;

import lombok.Data;

@Data
public class ClientResponseDTO {

    private Long   id;

    // Flattened from User
    private Long   userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;

    // Client profile
    private String  contactPerson;
    private String  image;
    private Boolean active;

    // Flattened company
    private Long   companyId;
    private String companyName;
    private String subdomain;

    // Flattened full address chain
    private Long   addressId;
    private String street;
    private String postOfficeName;
    private String postalCode;
    private String policeStationName;
    private String districtName;
    private String divisionName;
    private String countryName;
}
