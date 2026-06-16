package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.mapper.location.AddressMapper;
import com.StartupSAAS.dto.request.ClientRequest;
import com.StartupSAAS.dto.response.ClientResponse;
import com.StartupSAAS.entity.Client;
import com.StartupSAAS.entity.Company;
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
public class ClientMapper {
  private final CountryRepository countryRepository;
  private final DivisionRepository divisionRepository;
  private final DistrictRepository districtRepository;
  private final PoliceStationRepository policeStationRepository;
  private final PostOfficeRepository postOfficeRepository;
  private final AddressRepository addressRepository;
  private final AddressMapper addressMapper;
  private final CompanyRepository companyRepository;

  // DTO -> ENTITY (User)
  public User toEntity(ClientRequest req, Company company, PasswordEncoder encoder) {

    Address address = null;

    // Only build address if country is provided
    if (req.getCountry() != null && !req.getCountry().isBlank()) {
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

    return User.builder()
        .name(req.getName())
        .email(req.getEmail())
        .password(encoder.encode(req.getPassword()))
        .phone(req.getPhone())
        .role(Role.CLIENT)
        .address(address)
        .isActive(true)
        .build();
  }

  // ENTITY -> RESPONSE

  public ClientResponse toResponse(Client client) {
    ClientResponse.ClientResponseBuilder cb =
        ClientResponse.builder()
            .id(client.getId())
            .billingAddress(client.getBillingAddress());

    if (client.getUser() != null) {
      cb.name(client.getUser().getName());
      cb.email(client.getUser().getEmail());
      cb.phone(client.getUser().getPhone());
      cb.image(client.getUser().getImage());

      if (client.getUser().getAddress() != null) {
        Address address =
            addressRepository.findById(client.getUser().getAddress().getId()).orElse(null);
        if (address != null) {
          cb.address(addressMapper.toDTO(address));
        }
      }
    }

    if (client.getCompany() != null) {
      cb.companyId(client.getCompany().getId());
    }

    return cb.build();
  }

  public Client toClient(ClientRequest cr) {
    Client client = new Client();
    client.setBillingAddress(cr.getBillingAddress());
    return client;
  }
}
