package com.StartupSAAS.location.mapper;

import com.StartupSAAS.location.response.DivisionResponse;
import com.StartupSAAS.location.entity.Division;

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
