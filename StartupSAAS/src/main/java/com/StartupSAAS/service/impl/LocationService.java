package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.entity.address.*;
import com.StartupSAAS.repository.location.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

  private final CountryRepository countryRepository;
  private final DivisionRepository divisionRepository;
  private final DistrictRepository districtRepository;
  private final PoliceStationRepository policeStationRepository;
  private final PostOfficeRepository postOfficeRepository;

  public PostOffice resolvePostOffice(EmployeeRequest req) {

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
            .orElseThrow(() -> new RuntimeException("Police station not found"));

    return postOfficeRepository
        .findByNameAndPoliceStation(req.getPostOffice(), ps)
        .orElseThrow(() -> new RuntimeException("Post office not found"));
  }
}
