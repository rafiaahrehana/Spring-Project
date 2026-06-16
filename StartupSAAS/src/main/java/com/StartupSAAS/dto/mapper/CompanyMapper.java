package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.mapper.location.AddressMapper;
import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.address.Address;
import com.StartupSAAS.entity.address.Country;
import com.StartupSAAS.entity.address.District;
import com.StartupSAAS.entity.address.Division;
import com.StartupSAAS.entity.address.PoliceStation;
import com.StartupSAAS.entity.address.PostOffice;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.repository.location.AddressRepository;
import com.StartupSAAS.repository.location.CountryRepository;
import com.StartupSAAS.repository.location.DistrictRepository;
import com.StartupSAAS.repository.location.DivisionRepository;
import com.StartupSAAS.repository.location.PoliceStationRepository;
import com.StartupSAAS.repository.location.PostOfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyMapper {

  private final CountryRepository countryRepository;
  private final DivisionRepository divisionRepository;
  private final DistrictRepository districtRepository;
  private final PoliceStationRepository policeStationRepository;
  private final PostOfficeRepository postOfficeRepository;
  private final AddressRepository addressRepository;
  private final AddressMapper addressMapper;

  // Request DTO -> User Entity (Owner)
  public User toUser(CompanyRequest req, PasswordEncoder encoder) {
    Address address = null;

    if (req.getHouseNo() != null || req.getRoad() != null || req.getPostOffice() != null) {

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

      address = new Address();
      address.setHouseNo(req.getHouseNo());
      address.setRoad(req.getRoad());
      address.setPostOffice(po);
    }

    Role assignedRole = req.getRole() != null ? req.getRole() : Role.COMPANY_OWNER;

    return User.builder()
        .name(req.getName())
        .email(req.getEmail())
        .password(encoder.encode(req.getPassword()))
        .phone(req.getPhone())
        .role(assignedRole)
        .address(address)
        .isActive(true)
        .build();
  }

  // Request DTO -> Company Entity
  public Company toCompany(CompanyRequest request) {
    Company company = new Company();
    company.setName(request.getName());
    company.setEmail(request.getEmail());
    company.setPhone(request.getPhone());
    company.setSubdomain(request.getSubdomain());
    company.setWebsite(request.getWebsite());
    company.setSubscriptionPlan(
        request.getSubscriptionPlan() != null
            ? request.getSubscriptionPlan()
            : SubscriptionPlan.FREE);
    return company;
  }

  // Entity -> Response DTO
  public CompanyResponse toDTO(Company company) {
    User user = company.getUser();

    CompanyResponse response =
        CompanyResponse.builder()
            .id(company.getId())
            .name(company.getName())
            .email(company.getEmail())
            .phone(company.getPhone())
            .subdomain(company.getSubdomain())
            .logo(company.getLogo())
            .website(company.getWebsite())
            .subscriptionPlan(company.getSubscriptionPlan())
            .ownerId(user != null ? String.valueOf(user.getId()) : null)
            .role(user != null ? user.getRole() : null)
            .build();

    if (user != null && user.getAddress() != null) {
      Address address = addressRepository.findById(user.getAddress().getId()).orElseThrow();
      response.setAddress(addressMapper.toDTO(address));
    }

    return response;
  }
}
