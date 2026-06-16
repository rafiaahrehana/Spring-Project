package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.mapper.location.AddressMapper;
import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.address.*;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.repository.CompanyRepository;
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
  private final AddressRepository addressRepository;
  private final AddressMapper addressMapper;
  private final CompanyRepository companyRepository;

  // DTO -> ENTITY
  public User toEntity(EmployeeRequest req, Company company, PasswordEncoder encoder) {

    Country country =
        countryRepository
            .findByName(req.getCountry())
            .orElseThrow(() -> new RuntimeException("Country not found"));

    Division division =
        divisionRepository
            .findByNameAndCountry(req.getDivision(), country)
            .orElseThrow(() -> new RuntimeException("Division not found"));

    District district =
        districtRepository
            .findByNameAndDivision(req.getDistrict(), division)
            .orElseThrow(() -> new RuntimeException("District not found"));

    PoliceStation ps =
        policeStationRepository
            .findByNameAndDistrict(req.getPoliceStation(), district)
            .orElseThrow(() -> new RuntimeException("Police Station not found"));

    PostOffice po =
        postOfficeRepository
            .findByNameAndPoliceStation(req.getPostOffice(), ps)
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
        .address(address)
        .isActive(true)
        .build();
  }

  // ENTITY -> RESPONSE

  public EmployeeResponse toResponse(Employee emp) {

    EmployeeResponse er = new EmployeeResponse();
    er.setId(emp.getId());
    er.setDesignation(emp.getDesignation());

    if (emp.getUser() != null) {
      er.setName(emp.getUser().getName());
      er.setEmail(emp.getUser().getEmail());
      er.setPhone(emp.getUser().getPhone());
      er.setImage(emp.getUser().getImage());
      er.setRole(emp.getUser().getRole());

      if (emp.getUser().getAddress() != null) {
        Address address =
            addressRepository.findById(emp.getUser().getAddress().getId()).orElseThrow();
        er.setAddress(addressMapper.toDTO(address));
      }
    }

    if (emp.getCompany() != null) {
      Company company = companyRepository.findById(emp.getCompany().getId()).orElseThrow();
      er.setCompanyId(company.getId());
      er.setCompanyName(company.getName());
    }

    return er;
  }

  public Employee toEmployee(EmployeeRequest er) {
    Employee emp = new Employee();
    emp.setDesignation(er.getDesignation());
    return emp;
  }

  public User toUser(EmployeeRequest er) {
    User user = new User();
    user.setEmail(er.getEmail());
    user.setPassword(er.getPassword());
    user.setRole(er.getRole());

    return user;
  }
}
