package com.StartupSAAS.dto.request;

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
public class CompanyRequest {

  private String name;
  private String email;
  private String password;
  private String phone;
  private String subdomain;
  private String ownerId;
  private Role role;
  private String logo;
  private String website;
  private SubscriptionPlan subscriptionPlan;

  // Address Data
  private String houseNo;
  private String road;
  private String postalCode;
  private String postOffice;
  private String policeStation;
  private String district;
  private String division;
  private String country;
}
