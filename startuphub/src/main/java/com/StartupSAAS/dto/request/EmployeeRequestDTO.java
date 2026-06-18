package com.StartupSAAS.dto.request;

import lombok.Data;

@Data
public class EmployeeRequestDTO {

    // User account fields
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;

    // Employee profile
    private String designation;
    private String department;
    private String nidNumber;
    private String emergencyContact;

    // Which company
    private Long companyId;

    // Address fields — frontend sends postOfficeId + street
    private Long postOfficeId;   // validates full chain automatically
    private String street;
}
