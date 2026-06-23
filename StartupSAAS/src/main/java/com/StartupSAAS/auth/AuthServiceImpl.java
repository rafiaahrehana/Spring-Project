package com.StartupSAAS.auth;

import com.StartupSAAS.dto.mapper.CompanyMapper;
import com.StartupSAAS.dto.request.CompanyRegisterRequest;
import com.StartupSAAS.dto.response.CompanyResponse;

import com.StartupSAAS.email.EmailBranding;
import com.StartupSAAS.email.EmailService;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.Wallet;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.repository.WalletRepository;
import com.StartupSAAS.security.JwtUtil;
import com.StartupSAAS.serviceImpl.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.StartupSAAS.location.entity.Address;
import com.StartupSAAS.location.entity.PoliceStation;
import com.StartupSAAS.location.repository.AddressRepository;
import com.StartupSAAS.location.repository.PoliceStationRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CompanyMapper companyMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final EmailBranding emailBranding;
    private final ImageService imageService;
    private final AddressRepository addressRepository;
    private final PoliceStationRepository policeStationRepository;


    @Override
    @Transactional
    public CompanyResponse registerCompany(CompanyRegisterRequest request, MultipartFile logo) {
        if (companyRepository.existsBySubdomain(request.getSubdomain()))
            throw new BadRequestException("Subdomain already taken");

        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already registered");

        User owner = new User();
        owner.setFirstName(request.getFirstName());
        owner.setLastName(request.getLastName());
        owner.setEmail(request.getEmail());
        owner.setPhone(request.getPhone());
        if (request.getPoliceStationId() != null) {
            PoliceStation policeStation = policeStationRepository.findById(request.getPoliceStationId()).orElse(null);
            if (policeStation != null) {
                Address address = new Address();
                address.setHouseNo(request.getHouseNo());
                address.setRoad(request.getRoad());
                address.setPostOffice(request.getPostOffice());
                address.setPoliceStation(policeStation);
                addressRepository.save(address);
                owner.setAddress(address);
            }
        }
        owner.setPassword(passwordEncoder.encode(request.getPassword()));
        owner.setRole(Role.COMPANY_OWNER);
        owner.setActive(false);
        userRepository.save(owner);

        Company company = new Company();
        company.setCompanyName(request.getCompanyName());
        company.setSubdomain(request.getSubdomain());
        company.setCompanyEmail(request.getCompanyEmail());
        company.setCompanyPhone(request.getCompanyPhone());
        company.setWebsite(request.getWebsite());
        company.setUser(owner);
        company.setSubscriptionPlan(SubscriptionPlan.FREE);
        company.setActive(false);
        company.setEmailVerified(false);
        company.setVerificationToken(UUID.randomUUID().toString());
        company.setVerificationTokenExpiry(LocalDateTime.now().plusHours(1));

        if (logo != null && !logo.isEmpty())
            company.setLogo(imageService.upload(logo, "company", request.getCompanyName()));
        companyRepository.save(company);

        Wallet wallet = new Wallet();
        wallet.setBalance(0.0);
        wallet.setCompany(company);
        walletRepository.save(wallet);

        emailService.sendVerificationEmail(
                owner.getEmail(),
                owner.getFirstName(),
                company.getVerificationToken(),
                emailBranding.from(company)
        );

        return companyMapper.toDTO(company);
    }


    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with that email address"));

        if (user.isActive())
            throw new BadRequestException("This account is already verified. Please log in.");

        Company company = companyRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No company found for this account"));

        if (company.isEmailVerified())
            throw new BadRequestException("Email is already verified. Please log in.");

        company.setVerificationToken(UUID.randomUUID().toString());
        company.setVerificationTokenExpiry(LocalDateTime.now().plusHours(1));
        companyRepository.save(company);

        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getFirstName(),
                company.getVerificationToken(),
                emailBranding.from(company)
        );

        log.info("Verification email resent to {}", email);
    }


    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = (User) auth.getPrincipal();
        String token = jwtUtil.generateToken(user.getEmail());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        return response;
    }

    @Override
    @Transactional
    public String verifyEmail(String token) {
        Company company = companyRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired verification link"));

        if (company.isEmailVerified())
            return "Email already verified. You can log in.";

        if (company.getVerificationTokenExpiry() == null ||
                company.getVerificationTokenExpiry().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Verification link has expired. Please request a new one.");

        company.setEmailVerified(true);
        company.setActive(true);
        company.setSubscriptionStart(java.time.LocalDate.now());
        company.setSubscriptionEnd(java.time.LocalDate.now().plusDays(14));
        company.setVerificationToken(null);
        company.setVerificationTokenExpiry(null);
        companyRepository.save(company);

        // Also activate the owner user so they can log in
        User owner = company.getUser();
        owner.setActive(true);
        userRepository.save(owner);

        emailService.sendWelcomeEmail(
                owner.getEmail(),
                owner.getFirstName(),
                emailBranding.from(company)
        );

        return "Email verified successfully. Your 14-day trial has started. Welcome to StartupSAAS!";
    }
}