package com.startuphub.service.impl;

import com.startuphub.dto.request.ClientNoteRequest;
import com.startuphub.dto.request.LeadActivityRequest;
import com.startuphub.dto.request.LeadRequest;
import com.startuphub.dto.response.ClientNoteResponse;
import com.startuphub.dto.response.LeadActivityResponse;
import com.startuphub.dto.response.LeadResponse;
import com.startuphub.entity.Client;
import com.startuphub.entity.ClientNote;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.entity.HubService;
import com.startuphub.entity.Lead;
import com.startuphub.entity.LeadActivity;
import com.startuphub.entity.User;
import com.startuphub.enums.ClientStatus;
import com.startuphub.enums.LeadStatus;
import com.startuphub.enums.Role;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.Phase8Mapper;
import com.startuphub.repository.ClientNoteRepository;
import com.startuphub.repository.ClientRepository;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.HubServiceRepository;
import com.startuphub.repository.LeadActivityRepository;
import com.startuphub.repository.LeadRepository;
import com.startuphub.repository.UserRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.CrmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrmServiceImpl implements CrmService {

    private final LeadRepository         leadRepository;
    private final LeadActivityRepository activityRepository;
    private final ClientNoteRepository   clientNoteRepository;
    private final EmployeeRepository     employeeRepository;
    private final HubServiceRepository   hubServiceRepository;
    private final ClientRepository       clientRepository;
    private final UserRepository         userRepository;
    private final PasswordEncoder        passwordEncoder;
    private final SecurityUtil           securityUtil;

    // ── Leads ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public LeadResponse createLead(LeadRequest request) {
        Long companyId = requireCompanyId();
        Lead lead = Lead.builder()
            .contactName(request.contactName())
            .companyName(request.companyName())
            .email(request.email())
            .phone(request.phone())
            .industry(request.industry())
            .notes(request.notes())
            .status(request.status() != null ? request.status() : LeadStatus.NEW)
            .estimatedValue(request.estimatedValue())
            .company(companyRef(companyId))
            .build();

        if (request.assignedToId() != null) {
            lead.setAssignedTo(findEmployee(request.assignedToId(), companyId));
        }
        if (request.interestedServiceId() != null) {
            lead.setInterestedService(
                hubServiceRepository.findByIdAndCompanyId(request.interestedServiceId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Service not found: " + request.interestedServiceId())));
        }
        leadRepository.save(lead);
        log.info("Lead created: '{}' company={}", lead.getContactName(), companyId);
        return Phase8Mapper.toLeadResponse(lead);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadResponse getLeadById(Long id) {
        return Phase8Mapper.toLeadResponse(findLeadInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeadResponse> listLeads(LeadStatus status, Pageable pageable) {
        Long companyId = requireCompanyId();
        return (status != null
            ? leadRepository.findByCompanyIdAndStatus(companyId, status, pageable)
            : leadRepository.findByCompanyId(companyId, pageable))
            .map(Phase8Mapper::toLeadResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeadResponse> listMyLeads(Pageable pageable) {
        Long companyId = requireCompanyId();
        Employee emp = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));
        return leadRepository.findByCompanyIdAndAssignedToId(companyId, emp.getId(), pageable)
            .map(Phase8Mapper::toLeadResponse);
    }

    @Override
    @Transactional
    public LeadResponse updateLead(Long id, LeadRequest request) {
        Long companyId = requireCompanyId();
        Lead lead = findLeadInTenant(id);
        if (lead.getStatus() == LeadStatus.WON || lead.getStatus() == LeadStatus.LOST) {
            throw new BadRequestException("Cannot edit a closed lead");
        }
        if (request.contactName()     != null) lead.setContactName(request.contactName());
        if (request.companyName()     != null) lead.setCompanyName(request.companyName());
        if (request.email()           != null) lead.setEmail(request.email());
        if (request.phone()           != null) lead.setPhone(request.phone());
        if (request.industry()        != null) lead.setIndustry(request.industry());
        if (request.notes()           != null) lead.setNotes(request.notes());
        if (request.status()          != null) lead.setStatus(request.status());
        if (request.estimatedValue()  != null) lead.setEstimatedValue(request.estimatedValue());
        if (request.assignedToId()    != null) lead.setAssignedTo(findEmployee(request.assignedToId(), companyId));
        return Phase8Mapper.toLeadResponse(lead);
    }

    @Override
    @Transactional
    public LeadResponse convertLead(Long id) {
        Long companyId = requireCompanyId();
        Lead lead = findLeadInTenant(id);
        if (lead.getStatus() == LeadStatus.WON) {
            throw new BadRequestException("Lead is already converted");
        }
        if (lead.getEmail() == null || lead.getEmail().isBlank()) {
            throw new BadRequestException("Lead must have an email address to be converted");
        }
        if (userRepository.existsByEmail(lead.getEmail())) {
            throw new BadRequestException("A user with this email already exists");
        }

        // Create a portal user for the lead
        User user = User.builder()
            .firstName(lead.getContactName().split(" ")[0])
            .lastName(lead.getContactName().contains(" ")
                ? lead.getContactName().substring(lead.getContactName().indexOf(' ') + 1) : "")
            .email(lead.getEmail().toLowerCase().trim())
            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
            .role(Role.CLIENT)
            .active(true)
            .emailVerified(false)
            .build();
        userRepository.save(user);

        Client client = Client.builder()
            .user(user)
            .company(companyRef(companyId))
            .clientCompanyName(lead.getCompanyName())
            .industry(lead.getIndustry())
            .status(ClientStatus.ACTIVE)
            .accountManager(lead.getAssignedTo())
            .build();
        clientRepository.save(client);

        lead.setStatus(LeadStatus.WON);
        lead.setConvertedAt(LocalDateTime.now());
        lead.setConvertedClient(client);

        log.info("Lead converted: leadId={} clientId={} company={}", id, client.getId(), companyId);
        return Phase8Mapper.toLeadResponse(lead);
    }

    @Override
    @Transactional
    public void deleteLead(Long id) {
        Lead lead = findLeadInTenant(id);
        lead.softDelete();
    }

    // ── Lead Activities ───────────────────────────────────────────

    @Override
    @Transactional
    public LeadActivityResponse addActivity(Long leadId, LeadActivityRequest request) {
        Long companyId = requireCompanyId();
        Lead lead = findLeadInTenant(leadId);
        User currentUser = securityUtil.getCurrentUser();
        LeadActivity activity = LeadActivity.builder()
            .lead(lead)
            .company(companyRef(companyId))
            .activityType(request.activityType())
            .subject(request.subject())
            .description(request.description())
            .activityAt(request.activityAt() != null ? request.activityAt() : LocalDateTime.now())
            .nextFollowUp(request.nextFollowUp())
            .createdBy(currentUser)
            .build();
        activityRepository.save(activity);
        return Phase8Mapper.toLeadActivityResponse(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeadActivityResponse> getActivities(Long leadId, Pageable pageable) {
        findLeadInTenant(leadId);
        return activityRepository.findByLeadIdOrderByActivityAtDesc(leadId, pageable)
            .map(Phase8Mapper::toLeadActivityResponse);
    }

    @Override
    @Transactional
    public void deleteActivity(Long leadId, Long activityId) {
        findLeadInTenant(leadId);
        LeadActivity activity = activityRepository.findByIdAndCompanyId(activityId, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + activityId));
        activity.softDelete();
    }

    // ── Client Notes ──────────────────────────────────────────────

    @Override
    @Transactional
    public ClientNoteResponse addClientNote(Long clientId, ClientNoteRequest request) {
        Long companyId = requireCompanyId();
        Client client = clientRepository.findByIdAndCompanyId(clientId, companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + clientId));
        ClientNote note = ClientNote.builder()
            .content(request.content())
            .followUpAt(request.followUpAt())
            .client(client)
            .company(companyRef(companyId))
            .createdBy(securityUtil.getCurrentUser())
            .build();
        clientNoteRepository.save(note);
        return Phase8Mapper.toClientNoteResponse(note);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientNoteResponse> getClientNotes(Long clientId, Pageable pageable) {
        clientRepository.findByIdAndCompanyId(clientId, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + clientId));
        return clientNoteRepository.findByClientIdOrderByCreatedAtDesc(clientId, pageable)
            .map(Phase8Mapper::toClientNoteResponse);
    }

    @Override
    @Transactional
    public void deleteClientNote(Long clientId, Long noteId) {
        ClientNote note = clientNoteRepository.findByIdAndCompanyId(noteId, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Note not found: " + noteId));
        note.softDelete();
    }

    // ── Private helpers ───────────────────────────────────────────

    private Lead findLeadInTenant(Long id) {
        return leadRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found: " + id));
    }

    private Employee findEmployee(Long employeeId, Long companyId) {
        return employeeRepository.findByIdAndCompanyId(employeeId, companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }

    private Company companyRef(Long companyId) {
        Company c = new Company(); c.setId(companyId); return c;
    }
}
