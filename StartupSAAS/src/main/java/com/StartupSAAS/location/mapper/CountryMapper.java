package com.StartupSAAS.location.mapper;

import com.StartupSAAS.location.entity.Country;
import com.StartupSAAS.location.response.CountryResponse;

public class CountryMapper {

  public static CountryResponse toDTO(Country country) {
    if (country == null) return null;

    return CountryResponse.builder()
            .id(country.getId())
            .name(country.getName())
            .code(country.getCode())
            .build();
  }
}