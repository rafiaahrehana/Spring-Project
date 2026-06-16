package com.StartupSAAS.dto.response;

import com.StartupSAAS.dto.response.location.AddressResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
