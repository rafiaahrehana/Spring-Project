package com.StartupSAAS.dto.response.location;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PoliceStationResponse {
  private Long id;
  private String name;
  private DistrictResponse district;
}
