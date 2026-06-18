package com.StartupSAAS.dto.request;

import lombok.Data;

@Data
public class ClientRequestDTO {

    // User account fields
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;

    // Client profile
    private String contactPerson;

    // Which company
    private Long companyId;

    // Address fields
    private Long postOfficeId;   // validates full chain automatically
    private String street;
}
