package com.StartupSAAS.location.request;

import lombok.Data;

@Data
public class AddressRequest {
  private String houseNo;
  private String road;
  private String postOffice;
  private Long policeStationId;
}
