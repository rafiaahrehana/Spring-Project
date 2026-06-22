package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.SubscriptionPlan;
import lombok.Data;

@Data
public class CompanyRequest {
  // company fields
  private String companyName;
  private String companyEmail;
  private String companyPhone;
  private String subdomain;
  private String website;
  private SubscriptionPlan subscriptionPlan;

  // owner fields
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String phone;

  // address fields
  private String houseNo;
  private String road;
  private String postOffice;
  private Long policeStationId;
  private Long districtId;
  private Long divisionId;
  private Long countryId;
}