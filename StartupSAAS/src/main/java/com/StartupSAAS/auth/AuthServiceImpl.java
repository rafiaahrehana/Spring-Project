package com.StartupSAAS.auth;

import com.StartupSAAS.dto.mapper.CompanyMapper;
import com.StartupSAAS.dto.request.LoginRequest;
import com.StartupSAAS.dto.request.CompanyRegisterRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.dto.response.LoginResponse;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CompanyMapper companyMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    private static final int TRIAL_DAYS = 14;

    @Override
    @Transactional
    public CompanyResponse registerCompany(CompanyRegisterRequest request) {
        if (companyRepository.existsBySubdomain(request.getSubdomain()))
            throw new BadRequestException("Subdomain already taken");

        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already registered");

        User owner = new User();
        owner.setFirstName(request.getFirstName());
        owner.setLastName(request.getLastName());
        owner.setEmail(request.getEmail());
        owner.setPhone(request.getPhone());
        owner.setPassword(passwordEncoder.encode(request.getPassword()));
        owner.setRole(Role.COMPANY_OWNER);
        owner.setActive(true);
        userRepository.save(owner);

        Company company = new Company();
        company.setCompanyName(request.getCompanyName());
        company.setSubdomain(request.getSubdomain());
        company.setCompanyPhone(request.getCompanyPhone());
        company.setWebsite(request.getWebsite());
        company.setUser(owner);
        company.setSubscriptionPlan(SubscriptionPlan.FREE);
        company.setActive(true);
        company.setEmailVerified(false);
        company.setSubscriptionStart(LocalDate.now());
        company.setSubscriptionEnd(LocalDate.now().plusDays(TRIAL_DAYS));
        companyRepository.save(company);

        Wallet wallet = new Wallet();
        wallet.setBalance(0.0);
        wallet.setCompany(company);
        walletRepository.save(wallet);

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