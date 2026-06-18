package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.EmployeeResponseDTO;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.address.*;

public class EmployeeMapper {

    public static EmployeeResponseDTO toDTO(Employee employee) {

        EmployeeResponseDTO dto = new EmployeeResponseDTO();

        dto.setId(employee.getId());
        dto.setDesignation(employee.getDesignation());
        dto.setDepartment(employee.getDepartment());
        dto.setImage(employee.getImage());
        dto.setNidNumber(employee.getNidNumber());
        dto.setEmergencyContact(employee.getEmergencyContact());
        dto.setActive(employee.getActive());

        // Flatten User fields
        User user = employee.getUser();
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
        Company company = employee.getCompany();
        if (company != null) {
            dto.setCompanyId(company.getId());
            dto.setCompanyName(company.getName());
            dto.setSubdomain(company.getSubdomain());
        }

        // Flatten full Address chain
        // Address → PostOffice (postalCode) → PoliceStation → District → Division → Country
        Address address = employee.getAddress();
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
