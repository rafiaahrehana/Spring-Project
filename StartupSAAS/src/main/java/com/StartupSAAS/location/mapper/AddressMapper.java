package com.StartupSAAS.location.mapper;

import com.StartupSAAS.location.entity.Address;
import com.StartupSAAS.location.entity.PostOffice;
import com.StartupSAAS.location.request.AddressRequest;
import com.StartupSAAS.location.response.AddressResponse;

public class AddressMapper {

    public static AddressResponse toDTO(Address address) {
        if (address == null) return null;

        return AddressResponse.builder()
                .id(address.getId())
                .houseNo(address.getHouseNo())
                .road(address.getRoad())
                .postOffice(PostOfficeMapper.toDTO(address.getPostOffice()))
                .build();
    }

    public static Address toEntity(AddressRequest request, PostOffice postOffice) {
        if (request == null) return null;

        Address address = new Address();
        address.setHouseNo(request.getHouseNo());
        address.setRoad(request.getRoad());
        address.setPostOffice(postOffice);
        return address;
    }
}