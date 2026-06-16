package com.StartupSAAS.dto.mapper.location;

import com.StartupSAAS.dto.response.location.DivisionResponse;
import com.StartupSAAS.entity.address.Division;

public class DivisionMapper {

  public static DivisionResponse toDTO(Division division) {

    if (division == null) {
      return null;
    }

    return DivisionResponse.builder()
        .id(division.getId())
        .name(division.getName())
        .country(CountryMapper.toDTO(division.getCountry()))
        .build();
  }
}
