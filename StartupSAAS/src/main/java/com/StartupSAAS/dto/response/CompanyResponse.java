package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.location.response.AddressResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResponse {
  private Long id;
  private String companyName;
  private String companyEmail;
  private String companyPhone;
  private String subdomain;
  private String logo;
  private String website;
  private SubscriptionPlan subscriptionPlan;
  private Long ownerId;
  private String ownerName;
  private AddressResponse address;
}