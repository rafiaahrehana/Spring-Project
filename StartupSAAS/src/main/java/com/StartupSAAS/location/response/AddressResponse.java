package com.StartupSAAS.location.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {

  private Long id;
  private String houseNo;
  private String road;
  private String postOffice;
  private PoliceStationResponse policeStation;
}
