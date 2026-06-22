package com.StartupSAAS.auth;

import com.StartupSAAS.dto.mapper.CompanyMapper;
import com.StartupSAAS.dto.request.CompanyRegisterRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.Wallet;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.repository.WalletRepository;
import com.StartupSAAS.security.JwtUtil;
import com.StartupSAAS.serviceImpl.EmailService;
import com.StartupSAAS.serviceImpl.ImageService;
import jakarta.mail.MessagingException;
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
            PoliceStation ps = policeStationRepository.findById(request.getPoliceStationId())
                    .orElse(null);
            if (ps != null) {
                Address address = new Address();
                address.setHouseNo(request.getHouseNo());
                address.setRoad(request.getRoad());
                address.setPostOffice(request.getPostOffice());
                address.setPoliceStation(ps);
                addressRepository.save(address);
                owner.setAddress(address);
            }
        }
        owner.setPassword(passwordEncoder.encode(request.getPassword()));
        owner.setRole(Role.COMPANY_OWNER);
        owner.setActive(true);

        userRepository.save(owner);

        // Company stays inactive and on no plan until the owner verifies their email.
        // Trial period only starts once verification completes (see EmailVerificationController).
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
        companyRepository.save(company);

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
}
