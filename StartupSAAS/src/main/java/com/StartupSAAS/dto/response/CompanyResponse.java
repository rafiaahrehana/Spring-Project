package com.StartupSAAS.dto.response;

import com.StartupSAAS.dto.response.location.AddressResponse;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.enums.SubscriptionPlan;
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
  private String subdomain;
  private String ownerId;
  private Role role;
  private String logo;
  private String website;
  private SubscriptionPlan subscriptionPlan;

  private AddressResponse address;
}
