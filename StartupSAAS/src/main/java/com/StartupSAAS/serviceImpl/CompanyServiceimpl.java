package com.StartupSAAS.serviceImpl;

import com.StartupSAAS.dto.mapper.CompanyMapper;
import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.location.entity.*;
import com.StartupSAAS.location.repository.*;
import com.StartupSAAS.repository.ClientRepository;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.EmployeeRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.CompanyService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final CompanyMapper companyMapper;
  private final PasswordEncoder passwordEncoder;
  private final ImageService imageService;
  private final ClientRepository clientRepository;
  private final EmployeeRepository employeeRepository;
  private final CountryRepository countryRepository;
  private final DivisionRepository divisionRepository;
  private final DistrictRepository districtRepository;
  private final PoliceStationRepository policeStationRepository;
  private final PostOfficeRepository postOfficeRepository;

  @Override
  @Transactional
  public CompanyResponse createCompany(CompanyRequest request, MultipartFile logo) {
    if (companyRepository.existsBySubdomain(request.getSubdomain()))
      throw new BadRequestException("Subdomain already exists");

    if (userRepository.existsByEmail(request.getEmail()))
      throw new BadRequestException("Email already exists");

    Address address = null;
    if (request.getPostOfficeId() != null) {
      Country country = countryRepository.findById(request.getCountryId())
              .orElseThrow(() -> new BadRequestException("Country not found"));

      Division division = divisionRepository.findByIdAndCountryId(request.getDivisionId(), country.getId())
              .orElseThrow(() -> new BadRequestException("Invalid division for selected country"));

      District district = districtRepository.findByIdAndDivisionId(request.getDistrictId(), division.getId())
              .orElseThrow(() -> new BadRequestException("Invalid district for selected division"));

      PoliceStation policeStation = policeStationRepository.findByIdAndDistrictId(request.getPoliceStationId(), district.getId())
              .orElseThrow(() -> new BadRequestException("Invalid police station for selected district"));

      PostOffice postOffice = postOfficeRepository.findByIdAndPoliceStationId(request.getPostOfficeId(), policeStation.getId())
              .orElseThrow(() -> new BadRequestException("Invalid post office for selected police station"));

      address = new Address();
      address.setHouseNo(request.getHouseNo());
      address.setRoad(request.getRoad());
      address.setPostOffice(postOffice);
    }

    User owner = companyMapper.toUser(request, passwordEncoder);
    owner.setAddress(address);
    userRepository.save(owner);

    Company company = companyMapper.toCompany(request);
    company.setUser(owner);
    if (logo != null && !logo.isEmpty())
      company.setLogo(imageService.upload(logo, "company", request.getCompanyName()));
    companyRepository.save(company);

    return companyMapper.toDTO(company);
  }

  @Override
  public CompanyResponse getCompanyById(Long id) {
    return companyMapper.toDTO(
            companyRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found")));
  }

  @Override
  @Transactional
  public CompanyResponse updateCompany(Long id, CompanyRequest request, MultipartFile logo) {
    Company company = companyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

    company.setCompanyName(request.getCompanyName());
    company.setCompanyEmail(request.getCompanyEmail());
    company.setCompanyPhone(request.getCompanyPhone());
    company.setSubdomain(request.getSubdomain());
    company.setWebsite(request.getWebsite());
    if (logo != null && !logo.isEmpty())
      company.setLogo(imageService.upload(logo, "company", request.getCompanyName()));

    return companyMapper.toDTO(company);
  }

  @Override
  public List<CompanyResponse> getAllCompanies() {
    return companyRepository.findAll().stream()
            .map(companyMapper::toDTO)
            .collect(Collectors.toList());
  }

  @Override
  public Page<CompanyResponse> searchCompanies(String query, Pageable pageable) {
    return companyRepository.findByCompanyNameContainingIgnoreCase(query, pageable)
            .map(companyMapper::toDTO);
  }

  @Override
  public Page<CompanyResponse> getCompaniesByPackage(SubscriptionPlan subscriptionPlan, Pageable pageable) {
    return companyRepository.findBySubscriptionPlan(subscriptionPlan, pageable)
            .map(companyMapper::toDTO);
  }

  @Override
  @Transactional
  public void deleteCompany(Long id) {
    Company company = companyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

    if (employeeRepository.existsByCompanyId(id))
      throw new BadRequestException("Cannot delete company with existing employees");

    if (clientRepository.existsByCompanyId(id))
      throw new BadRequestException("Cannot delete company with existing clients");

    if (company.getUser() != null)
      userRepository.delete(company.getUser());

    companyRepository.delete(company);
  }
}