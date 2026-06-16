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

  // DTO -> ENTITY (User)
  public User toEntity(ClientRequest request, Company company, PasswordEncoder encoder) {
    Address address = null;
    if (request.getCountryId()!= null) {
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
    }

    return User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .phone(request.getPhone())
        .password(encoder.encode(request.getPassword()))
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
