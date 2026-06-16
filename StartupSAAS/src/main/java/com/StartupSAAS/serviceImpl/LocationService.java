package com.StartupSAAS.serviceImpl;

import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.location.entity.*;
import com.StartupSAAS.location.repository.CountryRepository;
import com.StartupSAAS.location.repository.DistrictRepository;
import com.StartupSAAS.location.repository.PoliceStationRepository;
import com.StartupSAAS.location.repository.PostOfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

  private final CountryRepository countryRepository;
  private final PostOfficeRepository.DivisionRepository divisionRepository;
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
