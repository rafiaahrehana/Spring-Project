package com.StartupSAAS.dto.mapper.location;

import com.StartupSAAS.dto.response.location.PoliceStationResponse;
import com.StartupSAAS.entity.address.PoliceStation;

public class PoliceStationMapper {

    public static PoliceStationResponse toDTO(PoliceStation policestation){

        if(policestation == null){
            return null;
        }

        return PoliceStationResponse.builder()
                .id(policestation.getId())
                .name(policestation.getName())
                .district(DistrictMapper.toDTO(policestation.getDistrict()))
                .build();
    }
}
