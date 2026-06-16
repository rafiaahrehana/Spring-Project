package com.StartupSAAS.dto.mapper.location;

import com.StartupSAAS.dto.response.location.PostOfficeResponse;
import com.StartupSAAS.entity.address.PostOffice;

public class PostOfficeMapper {

  public static PostOfficeResponse toDTO(PostOffice postOffice) {

    if (postOffice == null) {
      return null;
    }

    return PostOfficeResponse.builder()
        .id(postOffice.getId())
        .name(postOffice.getName())
        .postalCode(postOffice.getPostalCode())
        .policeStation(PoliceStationMapper.toDTO(postOffice.getPoliceStation()))
        .build();
  }
}
