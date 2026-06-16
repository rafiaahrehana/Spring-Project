package com.StartupSAAS.location.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DivisionResponse {
  private Long id;
  private String name;
  private CountryResponse country;
}
