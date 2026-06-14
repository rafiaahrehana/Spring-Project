package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String subdomain;
    private String ownerId;
    private Role role;
    private String logo;
    private String website;
}
