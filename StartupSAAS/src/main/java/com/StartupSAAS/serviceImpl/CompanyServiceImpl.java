package com.StartupSAAS.serviceImpl;

import com.StartupSAAS.dto.request.ActivateCompanyRequest;
import com.StartupSAAS.dto.mapper.CompanyMapper;
import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.Wallet;
import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.location.entity.Address;
import com.StartupSAAS.location.entity.PoliceStation;
import com.StartupSAAS.location.repository.PoliceStationRepository;
import com.StartupSAAS.repository.*;
import com.StartupSAAS.service.CompanyService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final PoliceStationRepository policeStationRepository;
    private final WalletRepository walletRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request, MultipartFile logo) {
        if (companyRepository.existsBySubdomain(request.getSubdomain()))
            throw new BadRequestException("Subdomain already exists");

        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already exists");

        Address address = null;
        if (request.getPoliceStationId() != null) {
            PoliceStation policeStation = policeStationRepository.findById(request.getPoliceStationId())
                    .orElseThrow(() -> new BadRequestException("Police station not found"));

            address = new Address();
            address.setHouseNo(request.getHouseNo());
            address.setRoad(request.getRoad());
            address.setPostOffice(request.getPostOffice());
            address.setPoliceStation(policeStation);
        }

        User owner = companyMapper.toUser(request, passwordEncoder);
        owner.setAddress(address);
        userRepository.save(owner);

        Company company = companyMapper.toCompany(request);
        company.setUser(owner);
        company.setActive(false);
        company.setVerificationToken(UUID.randomUUID().toString());
        company.setVerificationTokenExpiry(LocalDateTime.now().plusHours(1));
        if (logo != null && !logo.isEmpty())
            company.setLogo(imageService.upload(logo, "company", request.getCompanyName()));
        companyRepository.save(company);

        Wallet wallet = new Wallet();
        wallet.setBalance(0.0);
        wallet.setCompany(company);
        walletRepository.save(wallet);

        try {
            emailService.sendVerificationEmail(owner.getEmail(), owner.getFirstName(), company.getVerificationToken());
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}", owner.getEmail(), e);
        }

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
    @Transactional
    public CompanyResponse activateCompany(Long id, ActivateCompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        company.setActive(true);
        company.setSubscriptionPlan(request.getPlan());
        company.setSubscriptionStart(request.getStartDate());
        company.setSubscriptionEnd(request.getEndDate());
        company.setTrialReminderSent(false);

        return companyMapper.toDTO(company);
    }

    @Override
    @Transactional
    public CompanyResponse deactivateCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        company.setActive(false);
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