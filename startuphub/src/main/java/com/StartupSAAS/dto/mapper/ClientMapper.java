package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.ClientResponseDTO;
import com.StartupSAAS.entity.Client;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.address.*;

public class ClientMapper {

    public static ClientResponseDTO toDTO(Client client) {

        ClientResponseDTO dto = new ClientResponseDTO();

        dto.setId(client.getId());
        dto.setContactPerson(client.getContactPerson());
        dto.setImage(client.getImage());
        dto.setActive(client.getActive());

        // Flatten User fields
        User user = client.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setRole(user.getRole() != null
                    ? user.getRole().name() : null);
        }

        // Flatten Company fields
        Company company = client.getCompany();
        if (company != null) {
            dto.setCompanyId(company.getId());
            dto.setCompanyName(company.getName());
            dto.setSubdomain(company.getSubdomain());
        }

        // Flatten full Address chain
        // Address → PostOffice (postalCode) → PoliceStation → District → Division → Country
        Address address = client.getAddress();
        if (address != null) {
            dto.setAddressId(address.getId());
            dto.setStreet(address.getStreet());

            PostOffice postOffice = address.getPostOffice();
            if (postOffice != null) {
                dto.setPostOfficeName(postOffice.getName());
                dto.setPostalCode(postOffice.getPostalCode());

                PoliceStation policeStation = postOffice.getPoliceStation();
                if (policeStation != null) {
                    dto.setPoliceStationName(policeStation.getName());

                    District district = policeStation.getDistrict();
                    if (district != null) {
                        dto.setDistrictName(district.getName());

                        Division division = district.getDivision();
                        if (division != null) {
                            dto.setDivisionName(division.getName());

                            Country country = division.getCountry();
                            if (country != null) {
                                dto.setCountryName(country.getName());
                            }
                        }
                    }
                }
            }
        }

        return dto;
    }
}
