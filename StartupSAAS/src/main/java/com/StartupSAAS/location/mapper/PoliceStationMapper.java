package com.StartupSAAS.location.mapper;

import com.StartupSAAS.location.response.PoliceStationResponse;
import com.StartupSAAS.location.entity.PoliceStation;

public class PoliceStationMapper {

  public static PoliceStationResponse toDTO(PoliceStation policeStation) {

    if (policeStation == null) {
      return null;
    }

    return PoliceStationResponse.builder()
        .id(policeStation.getId())
        .name(policeStation.getName())
        .district(DistrictMapper.toDTO(policeStation.getDistrict()))
        .build();
  }
}
