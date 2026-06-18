package com.StartupSAAS.dto.response;

import lombok.Data;

@Data
public class EmployeeResponseDTO {

    private Long   id;

    // Flattened from User
    private Long   userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;

    // Employee profile
    private String  designation;
    private String  department;
    private String  image;
    private String  nidNumber;
    private String  emergencyContact;
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
