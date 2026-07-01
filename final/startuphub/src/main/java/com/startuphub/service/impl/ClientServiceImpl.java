package com.startuphub.service.impl;

import com.startuphub.dto.request.CreateClientRequest;
import com.startuphub.dto.request.UpdateClientRequest;
import com.startuphub.dto.response.ClientResponse;
import com.startuphub.entity.Client;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.entity.User;
import com.startuphub.enums.ClientStatus;
import com.startuphub.enums.Role;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.ClientMapper;
import com.startuphub.repository.ClientRepository;
import com.startuphub.repository.CompanyRepository;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.UserRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.ClientService;
import com.startuphub.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository             clientRepository;
    private final UserRepository               userRepository;
    private final CompanyRepository            companyRepository;
    private final EmployeeRepository           employeeRepository;
    private final PasswordEncoder              passwordEncoder;
    private final NotificationPreferenceService notificationPreferenceService;
    private final SecurityUtil                 securityUtil;

    @Override
    @Transactional
    public ClientResponse create(CreateClientRequest request) {
        Long companyId = requireCompanyId();

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("An account with this email already exists");
        }

        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        User user = User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email().toLowerCase().trim())
            .password(passwordEncoder.encode(request.password()))
            .phone(request.phone())
            .role(Role.CLIENT)
            .active(true)
            .emailVerified(true)
            .build();
        userRepository.save(user);

        Client client = Client.builder()
            .user(user)
            .company(company)
            .clientCompanyName(request.clientCompanyName())
            .industry(request.industry())
            .website(request.website())
            .taxId(request.taxId())
            .status(ClientStatus.ACTIVE)
            .build();

        if (request.accountManagerId() != null) {
            Employee am = employeeRepository
                .findByIdAndCompanyId(request.accountManagerId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Account manager not found: " + request.accountManagerId()));
            client.setAccountManager(am);
        }

        clientRepository.save(client);
        notificationPreferenceService.createDefaultsForUser(user.getId());

        log.info("Client created: userId={} company={}", user.getId(), companyId);
        return ClientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getById(Long id) {
        return ClientMapper.toResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getMyProfile() {
        User user = securityUtil.getCurrentUser();
        Client client = clientRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Client profile not found"));
        return ClientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponse> listAll(ClientStatus status, Pageable pageable) {
        Long companyId = requireCompanyId();
        Page<Client> page = status != null
            ? clientRepository.findByCompanyIdAndStatus(companyId, status, pageable)
            : clientRepository.findByCompanyId(companyId, pageable);
        return page.map(ClientMapper::toResponse);
    }

    @Override
    @Transactional
    public ClientResponse update(Long id, UpdateClientRequest request) {
        Long companyId = requireCompanyId();
        Client client = findInTenant(id);

        if (request.clientCompanyName()   != null) client.setClientCompanyName(request.clientCompanyName());
        if (request.industry()            != null) client.setIndustry(request.industry());
        if (request.website()             != null) client.setWebsite(request.website());
        if (request.taxId()               != null) client.setTaxId(request.taxId());
        if (request.status()              != null) client.setStatus(request.status());
        if (request.portalAccessEnabled() != null) client.setPortalAccessEnabled(request.portalAccessEnabled());

        if (request.accountManagerId() != null) {
            Employee am = employeeRepository
                .findByIdAndCompanyId(request.accountManagerId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Account manager not found: " + request.accountManagerId()));
            client.setAccountManager(am);
        }

        return ClientMapper.toResponse(client);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Client client = findInTenant(id);
        client.softDelete();

        User user = client.getUser();
        if (user != null) {
            user.setActive(false);
            user.softDelete();
            userRepository.save(user);
        }
        log.info("Client soft-deleted: id={}", id);
    }

    // ── Private helpers ───────────────────────────────────────────

    private Client findInTenant(Long id) {
        return clientRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + id));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }
}
