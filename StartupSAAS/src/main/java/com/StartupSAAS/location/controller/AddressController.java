package com.StartupSAAS.location.controller;

import com.StartupSAAS.location.entity.*;
import com.StartupSAAS.location.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final CountryRepository countryRepository;
    private final DivisionRepository divisionRepository;
    private final DistrictRepository districtRepository;
    private final PoliceStationRepository policeStationRepository;
    private final PostOfficeRepository postOfficeRepository;

    @GetMapping("/countries")
    public ResponseEntity<List<Country>> getCountries() {
        return ResponseEntity.ok(countryRepository.findAll());
    }

    @GetMapping("/divisions/{countryId}")
    public ResponseEntity<List<Division>> getDivisions(@PathVariable Long countryId) {
        return ResponseEntity.ok(divisionRepository.findByCountryId(countryId));
    }

    @GetMapping("/districts/{divisionId}")
    public ResponseEntity<List<District>> getDistricts(@PathVariable Long divisionId) {
        return ResponseEntity.ok(districtRepository.findByDivisionId(divisionId));
    }

    @GetMapping("/police-stations/{districtId}")
    public ResponseEntity<List<PoliceStation>> getPoliceStations(@PathVariable Long districtId) {
        return ResponseEntity.ok(policeStationRepository.findByDistrictId(districtId));
    }

    @GetMapping("/post-offices/{policeStationId}")
    public ResponseEntity<List<PostOffice>> getPostOffices(@PathVariable Long policeStationId) {
        return ResponseEntity.ok(postOfficeRepository.findByPoliceStationId(policeStationId));
    }
}
