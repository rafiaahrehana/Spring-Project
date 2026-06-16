package com.StartupSAAS.dto.mapper.location;

import com.StartupSAAS.dto.response.*;
import com.StartupSAAS.dto.response.location.AddressResponse;
import com.StartupSAAS.entity.address.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

  public AddressResponse toDTO(Address address) {

    if (address == null) {
      return null;
    }

    return AddressResponse.builder()
        .id(address.getId())
        .houseNo(address.getHouseNo())
        .road(address.getRoad())
        .postOffice(PostOfficeMapper.toDTO(address.getPostOffice()))
        .build();
  }
}
