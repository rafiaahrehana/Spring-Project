package com.StartupSAAS.serviceImpl;

import com.StartupSAAS.dto.mapper.CompanyMapper;
import com.StartupSAAS.dto.request.ActivateCompanyRequest;
import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
<<<<<<< HEAD
=======
import com.StartupSAAS.email.EmailBranding;
>>>>>>> 747485b4393350adcfaea3e85a357be7eafd6ff8
import com.StartupSAAS.email.EmailService;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.Wallet;
import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.location.entity.Address;
import com.StartupSAAS.location.entity.PoliceStation;
import com.StartupSAAS.location.repository.PoliceStationRepository;
import com.StartupSAAS.repository.ClientRepository;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.EmployeeRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.repository.WalletRepository;
import com.StartupSAAS.service.CompanyService;
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
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final WalletRepository walletRepository;
    private final PoliceStationRepository policeStationRepository;

    private final CompanyMapper companyMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final EmailService emailService;
    private final EmailBranding emailBranding;

    /**
     * Returns company or throws ResourceNotFoundException.
     */
    private Company findCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company not found with id : " + id));
    }

    /**
     * Validate uniqueness before company creation.
     */
    private void validateCompanyRegistration(CompanyRequest request) {

<<<<<<< HEAD
        if (companyRepository.existsBySubdomain(request.getSubdomain())) {
            throw new BadRequestException("Subdomain already exists.");
=======
        Address address = null;
        if (request.getPoliceStationId() != null) {
            PoliceStation policeStation = policeStationRepository.findById(request.getPoliceStationId())
                    .orElseThrow(() -> new BadRequestException("Police station not found"));
            address = new Address();
            address.setHouseNo(request.getHouseNo());
            address.setRoad(request.getRoad());
            address.setPostOffice(request.getPostOffice());
            address.setPoliceStation(policeStation);
>>>>>>> 747485b4393350adcfaea3e85a357be7eafd6ff8
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists.");
        }
    }

    /**
     * Build address from request.
     */
    private Address buildAddress(CompanyRequest request) {

        if (request.getPoliceStationId() == null) {
            return null;
        }

        PoliceStation policeStation = policeStationRepository.findById(request.getPoliceStationId())
                .orElseThrow(() ->
                        new BadRequestException("Police station not found."));

        Address address = new Address();
        address.setHouseNo(request.getHouseNo());
        address.setRoad(request.getRoad());
        address.setPostOffice(request.getPostOffice());
        address.setPoliceStation(policeStation);

        return address;
    }

    /**
     * Initialize newly created company.
     */
    private void initializeCompany(Company company) {

        company.setActive(false);
<<<<<<< HEAD

=======
        company.setEmailVerified(false);
>>>>>>> 747485b4393350adcfaea3e85a357be7eafd6ff8
        company.setVerificationToken(UUID.randomUUID().toString());

        company.setVerificationTokenExpiry(
                LocalDateTime.now().plusHours(1)
        );

        company.setTrialReminderSent(false);
    }

    /**
     * Upload company logo.
     */
    private String uploadLogo(MultipartFile logo, String companyName) {

        if (logo == null || logo.isEmpty()) {
            return null;
        }

        return imageService.upload(
                logo,
                "company",
                companyName
        );
    }

    /**
     * Create company wallet.
     */
    private void createWallet(Company company) {

        Wallet wallet = new Wallet();
        wallet.setBalance(0.0);
        wallet.setCompany(company);

        walletRepository.save(wallet);

<<<<<<< HEAD
        log.info("Wallet created for company '{}'", company.getCompanyName());
    }

    /**
     * Send email verification.
     */
    private void sendVerificationEmail(User owner, Company company) {

        try {

            emailService.sendVerificationEmail(
                    owner.getEmail(),
                    owner.getFirstName(),
                    company.getVerificationToken()
            );

            log.info("Verification email sent to {}", owner.getEmail());

        } catch (MessagingException ex) {

            log.error(
                    "Failed to send verification email to {}",
                    owner.getEmail(),
                    ex
            );
        }
=======
        emailService.sendVerificationEmail(
                owner.getEmail(),
                owner.getFirstName(),
                company.getVerificationToken(),
                emailBranding.from(company)
        );

        return companyMapper.toDTO(company);
>>>>>>> 747485b4393350adcfaea3e85a357be7eafd6ff8
    }

    /**
     * Update editable company information.
     */
    private void updateCompanyDetails(
            Company company,
            CompanyRequest request,
            MultipartFile logo
    ) {

        company.setCompanyName(request.getCompanyName());
        if (!company.getCompanyEmail().equals(request.getCompanyEmail())
                && companyRepository.existsByCompanyEmail(request.getCompanyEmail())) {

            throw new BadRequestException(
                    "Company email already exists."
            );
        }

        company.setCompanyEmail(request.getCompanyEmail());
        company.setCompanyPhone(request.getCompanyPhone());
        if (!company.getSubdomain().equals(request.getSubdomain())) {
            throw new BadRequestException(
                    "Company subdomain cannot be changed."
            );
        }
        company.setWebsite(request.getWebsite());

        String uploadedLogo = uploadLogo(
                logo,
                request.getCompanyName()
        );

        if (uploadedLogo != null) {
            company.setLogo(uploadedLogo);
        }
    }

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request, MultipartFile logo) {

        log.info("Starting company registration for '{}'", request.getCompanyName());

        // Validate registration request
        validateCompanyRegistration(request);

        // Build company owner's address
        Address address = buildAddress(request);

        // Create owner user
        User owner = companyMapper.toUser(request, passwordEncoder);
        owner.setAddress(address);

        userRepository.save(owner);

        log.info("Company owner created with email '{}'", owner.getEmail());

        // Create company
        Company company = companyMapper.toCompany(request);

        company.setUser(owner);

        initializeCompany(company);

        String logoUrl = uploadLogo(logo, request.getCompanyName());
        if (logoUrl != null) {
            company.setLogo(logoUrl);
        }

        companyRepository.save(company);

        log.info("Company '{}' created successfully.", company.getCompanyName());

        // Create default wallet
        createWallet(company);

        // Send verification email
        sendVerificationEmail(owner, company);

        return companyMapper.toDTO(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long id) {

        log.debug("Fetching company with id {}", id);

        Company company = findCompanyById(id);

        return companyMapper.toDTO(company);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> getAllCompanies() {

        log.debug("Fetching all companies");

        return companyRepository.findAll()
                .stream()
                .map(companyMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyResponse> searchCompanies(String query, Pageable pageable) {

        log.debug("Searching companies with keyword '{}'", query);

        return companyRepository
                .findByCompanyNameContainingIgnoreCase(query, pageable)
                .map(companyMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyResponse> getCompaniesByPackage(
            SubscriptionPlan subscriptionPlan,
            Pageable pageable
    ) {

        log.debug("Fetching companies with subscription plan {}", subscriptionPlan);

        return companyRepository
                .findBySubscriptionPlan(subscriptionPlan, pageable)
                .map(companyMapper::toDTO);
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(
            Long id,
            CompanyRequest request,
            MultipartFile logo
    ) {

        log.info("Updating company with id {}", id);

        Company company = findCompanyById(id);

        updateCompanyDetails(company, request, logo);

        companyRepository.save(company);

        log.info("Company '{}' updated successfully", company.getCompanyName());

        return companyMapper.toDTO(company);
    }

    @Override
    @Transactional
    public CompanyResponse activateCompany(
            Long id,
            ActivateCompanyRequest request
    ) {

        log.info("Activating company {}", id);

        Company company = findCompanyById(id);

        company.setActive(true);
        company.setSubscriptionPlan(request.getPlan());
        company.setSubscriptionStart(request.getStartDate());
        company.setSubscriptionEnd(request.getEndDate());
        company.setTrialReminderSent(false);

        companyRepository.save(company);

        log.info("Company '{}' activated", company.getCompanyName());

        return companyMapper.toDTO(company);
    }

    @Override
    @Transactional
    public CompanyResponse deactivateCompany(Long id) {

        log.info("Deactivating company {}", id);

        Company company = findCompanyById(id);

        company.setActive(false);

        companyRepository.save(company);

        log.info("Company '{}' deactivated", company.getCompanyName());

        return companyMapper.toDTO(company);
    }

    @Override
    @Transactional
    public void deleteCompany(Long id) {

        log.warn("Deleting company with id {}", id);

        Company company = findCompanyById(id);

        if (employeeRepository.existsByCompanyId(id)) {
            throw new BadRequestException(
                    "Cannot delete company with existing employees."
            );
        }

        if (clientRepository.existsByCompanyId(id)) {
            throw new BadRequestException(
                    "Cannot delete company with existing clients."
            );
        }

        if (company.getUser() != null) {
            userRepository.delete(company.getUser());
        }

        company.setActive(false);
        company.setDeleted(true);
        company.setDeletedAt(LocalDateTime.now());

        log.info("Company '{}' deleted successfully",
                company.getCompanyName());
    }
}