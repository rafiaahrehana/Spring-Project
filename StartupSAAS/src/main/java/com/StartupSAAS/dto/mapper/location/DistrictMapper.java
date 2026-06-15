package com.StartupSAAS.dto.mapper.location;

import com.StartupSAAS.dto.response.location.DistrictResponse;
import com.StartupSAAS.entity.address.District;

public class DistrictMapper {

    public static DistrictResponse toDTO(District district){

        if(district == null){
            return null;
        }
        return DistrictResponse.builder()
                .id(district.getId())
                .name(district.getName())
                .division(DivisionMapper.toDTO(district.getDivision()))
                .build();
    }
}