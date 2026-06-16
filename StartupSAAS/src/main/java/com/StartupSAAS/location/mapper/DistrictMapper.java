package com.StartupSAAS.location.mapper;

import com.StartupSAAS.location.response.DistrictResponse;
import com.StartupSAAS.location.entity.District;

public class DistrictMapper {

  public static DistrictResponse toDTO(District district) {

    if (district == null) {
      return null;
    }
    return DistrictResponse.builder()
        .id(district.getId())
        .name(district.getName())
        .division(DivisionMapper.toDTO(district.getDivision()))
        .build();
  }
}
