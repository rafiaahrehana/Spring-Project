package com.StartupSAAS.dto.mapper.location;

import com.StartupSAAS.dto.response.location.CountryResponse;
import com.StartupSAAS.entity.address.Country;

public class CountryMapper {

    public static CountryResponse toDTO(Country country){

        if(country == null){
            return null;
        }
        return CountryResponse.builder()
                .id(country.getId())
                .name(country.getName())
                .code(country.getCode())
                .build();
    }
}
