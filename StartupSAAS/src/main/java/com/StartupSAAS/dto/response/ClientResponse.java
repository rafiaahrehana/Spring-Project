package com.StartupSAAS.dto.response;

import com.StartupSAAS.location.response.AddressResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientResponse {
  private Long id;
  private String name;
  private String email;
  private String phone;
  private String image;
  private String billingAddress;
  private Long companyId;
  private AddressResponse address;
}