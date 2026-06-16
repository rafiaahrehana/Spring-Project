package com.StartupSAAS.location.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostOfficeResponse {

  private Long id;
  private String name;
  private String postalCode;
  private PoliceStationResponse policeStation;
}
