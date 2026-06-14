package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.address.*;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.repository.location.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {
    private final CountryRepository countryRepository;
    private final DivisionRepository divisionRepository;
    private final DistrictRepository districtRepository;
    private final PoliceStationRepository policeStationRepository;
    private final PostOfficeRepository postOfficeRepository;


    // DTO -> ENTITY
    public User toEntity(EmployeeRequest req, Company company, PasswordEncoder encoder) {

        Country country = countryRepository.findByName(req.getCountry())
                .orElseThrow(() -> new RuntimeException("Country not found"));

        Division division = divisionRepository.findByNameAndCountry(req.getDivision(), country)
                .orElseThrow(() -> new RuntimeException("Division not found"));

        District district = districtRepository.findByNameAndDivision(req.getDistrict(), division)
                .orElseThrow(() -> new RuntimeException("District not found"));

        PoliceStation ps = policeStationRepository.findByNameAndDistrict(req.getPoliceStation(), district)
                .orElseThrow(() -> new RuntimeException("Police Station not found"));

        PostOffice po = postOfficeRepository.findByNameAndPoliceStation(req.getPostOffice(), ps)
                .orElseThrow(() -> new RuntimeException("Post Office not found"));


        Address address = new Address();
        address.setHouseNo(req.getHouseNo());
        address.setRoad(req.getRoad());
        address.setPostOffice(po);


        return User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .role(Role.EMPLOYEE)
                .designation(req.getDesignation())
                .company(company)
                .address(address)
                .isActive(true)
                .build();
    }

    // ENTITY -> RESPONSE

    public EmployeeResponse toResponse(User user) {

        Address a = user.getAddress();
        PostOffice po = a.getPostOffice();
        PoliceStation ps = po.get();
        District d = ps.getDistrict();
        Division dv = d.getDivision();
        Country c = dv.getCountry();

        EmployeeResponse res = new EmployeeResponse();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());

        res.setRole(user.getRole().name());
        res.setDesignation(user.getDesignation().name());

        res.setCompanyName(user.getCompany().getName());

        res.setHouseNo(a.getHouseNo());
        res.setRoad(a.getRoad());

        res.setPostOffice(po.getName());
        res.setPoliceStation(ps.getName());
        res.setDistrict(d.getName());
        res.setDivision(dv.getName());
        res.setCountry(c.getName());

        return res;
    }
}