package com.StartupSAAS.location.mapper;

import com.StartupSAAS.location.entity.PostOffice;
import com.StartupSAAS.location.response.PostOfficeResponse;

public class PostOfficeMapper {

  public static PostOfficeResponse toDTO(PostOffice postOffice) {
    if (postOffice == null) return null;

    return PostOfficeResponse.builder()
            .id(postOffice.getId())
            .name(postOffice.getName())
            .postalCode(postOffice.getPostalCode())
            .policeStation(PoliceStationMapper.toDTO(postOffice.getPoliceStation()))
            .build();
  }
}