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
  public User toUser(EmployeeRequest request, Company company, PasswordEncoder encoder) {
    Address address = null;
    if (request.getCountryId() != null) {
      Country country = countryRepository.findById(request.getCountryId())
              .orElseThrow(() -> new RuntimeException("Country not found"));

      Division division = divisionRepository.findByIdAndCountryId(request.getDivisionID(), country.getId())
              .orElseThrow(() -> new RuntimeException("District not found"));

      District district = districtRepository.findByIdAndDivisionId(request.getDistrictId(), division.getId())
              .orElseThrow(() -> new RuntimeException("Invalid district"));


      PoliceStation policeStation = policeStationRepository.findByIdAndDistrictId(request.getPoliceStationId(), district.getId())
              .orElseThrow(() -> new RuntimeException("Invalid police station"));

      PostOffice postOffice = postOfficeRepository.findByIdAndPoliceStationId(request.getPostOfficeId(), policeStation.getId())
              .orElseThrow(() -> new RuntimeException("Invalid post office"));

      address = new Address();
      address.setHouseNo(request.getHouseNo());
      address.setRoad(request.getRoad());
      address.setPostOffice(postOffice);

      return User.builder()
              .name(request.getName())
              .email(request.getEmail())
              .phone(request.getPhone())
              .password(encoder.encode(request.getPassword()))
              .role(Role.EMPLOYEE)
              .address(address)
              .isActive(true)
              .build();
    }
  }
  // ENTITY -> RESPONSE

  public EmployeeResponse toResponse(Employee employee) {

    EmployeeResponse response = new EmployeeResponse();
    response.setId(employee.getId());

    if (employee.getUser() != null) {
      response.setName(employee.getUser().getName());
      response.setEmail(employee.getUser().getEmail());
      response.setPhone(employee.getUser().getPhone());
      response.setImage(employee.getUser().getImage());
      response.setRole(employee.getUser().getRole());

      if (employee.getUser().getAddress() != null)
        response.setAddress(addressMapper.toDTO( addressRepository.findById(employee.getUser().getAddress().getId()).orElseThrow()));

    }
    response.setDesignation(employee.getDesignation());

    if (employee.getCompany() != null) {
      Company company = companyRepository.findById(employee.getCompany().getId()).orElseThrow();
      response.setCompanyId(company.getId());
      response.setCompanyName(company.getName());
    }

    return response;
  }

  public Employee toEmployee(EmployeeRequest employeeRequest) {
    Employee employee = new Employee();
    employee.setDesignation(employeeRequest.getDesignation());
    return employee;
  }

}
