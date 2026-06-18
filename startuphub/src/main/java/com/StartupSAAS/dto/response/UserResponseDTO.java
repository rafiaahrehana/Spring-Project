package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.Role;
import lombok.Data;

@Data
public class UserResponseDTO {

    private Long   id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Role   role;

    // Flattened company info
    private Long   companyId;
    private String companyName;
    private String subdomain;

    private String  profilePictureUrl;
    private String  languagePref;
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private Boolean isActive;
}
