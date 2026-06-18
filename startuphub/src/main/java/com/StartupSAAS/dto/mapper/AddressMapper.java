package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.AddressResponseDTO;
import com.StartupSAAS.entity.address.*;

public class AddressMapper {

    public static AddressResponseDTO toDTO(Address address) {

        if (address == null) return null;

        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());

        // PostOffice
        PostOffice postOffice = address.getPostOffice();
        if (postOffice != null) {
            dto.setPostOfficeId(postOffice.getId());
            dto.setPostOfficeName(postOffice.getName());
            dto.setPostalCode(postOffice.getPostalCode());

            // PoliceStation
            PoliceStation policeStation = postOffice.getPoliceStation();
            if (policeStation != null) {
                dto.setPoliceStationId(policeStation.getId());
                dto.setPoliceStationName(policeStation.getName());

                // District
                District district = policeStation.getDistrict();
                if (district != null) {
                    dto.setDistrictId(district.getId());
                    dto.setDistrictName(district.getName());

                    // Division
                    Division division = district.getDivision();
                    if (division != null) {
                        dto.setDivisionId(division.getId());
                        dto.setDivisionName(division.getName());

                        // Country
                        Country country = division.getCountry();
                        if (country != null) {
                            dto.setCountryId(country.getId());
                            dto.setCountryName(country.getName());
                        }
                    }
                }
            }
        }

        return dto;
    }
}
