package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.ServiceRequestMapper;
import com.StartupSAAS.dto.request.ServiceRequestDTO;
import com.StartupSAAS.dto.request.ServiceRequestStatusDTO;
import com.StartupSAAS.dto.response.ServiceRequestResponseDTO;
import com.StartupSAAS.entity.*;
import com.StartupSAAS.enums.RequestStatus;
import com.StartupSAAS.repository.*;
import com.StartupSAAS.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final HubServiceRepository hubServiceRepository;
    private final ClientRepository clientRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    @Override
    public ServiceRequestResponseDTO create(ServiceRequestDTO dto) {

        HubService hubService = hubServiceRepository.findById(dto.getHubServiceId())
                .orElseThrow(() -> new RuntimeException(
                        "HubService not found with id: " + dto.getHubServiceId()));

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException(
                        "Client not found with id: " + dto.getClientId()));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + dto.getCompanyId()));

        ServiceRequest sr = new ServiceRequest();
        sr.setTitle(dto.getTitle());
        sr.setDescription(dto.getDescription());
        sr.setPriority(dto.getPriority() != null ? dto.getPriority()
                : hubService.getDefaultPriority());
        sr.setDeadline(dto.getDeadline());
        sr.setAgreedPrice(dto.getAgreedPrice() != null ? dto.getAgreedPrice()
                : hubService.getPrice());
        sr.setSlaHours(dto.getSlaHours());
        sr.setStatus(RequestStatus.PENDING);
        sr.setCurrentStage(0);
        sr.setHubService(hubService);
        sr.setClient(client);
        sr.setCompany(company);

        // Assign employee immediately if provided
        if (dto.getAssignedEmployeeId() != null) {
            Employee emp = employeeRepository.findById(dto.getAssignedEmployeeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found with id: " + dto.getAssignedEmployeeId()));
            sr.setAssignedEmployee(emp);
            sr.setStatus(RequestStatus.ASSIGNED);
            sr.setAssignedAt(LocalDateTime.now());
        }

        ServiceRequest saved = serviceRequestRepository.save(sr);
        return ServiceRequestMapper.toDTO(
                serviceRequestRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequestResponseDTO> getAll() {
        return serviceRequestRepository.findAllWithDetails()
                .stream().map(ServiceRequestMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceRequestResponseDTO getById(Long id) {
        return ServiceRequestMapper.toDTO(
                serviceRequestRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new RuntimeException(
                                "ServiceRequest not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequestResponseDTO> getByCompany(Long companyId) {
        return serviceRequestRepository.findByCompanyId(companyId)
                .stream().map(ServiceRequestMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequestResponseDTO> getByClient(Long clientId) {
        return serviceRequestRepository.findByClientId(clientId)
                .stream().map(ServiceRequestMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequestResponseDTO> getByEmployee(Long employeeId) {
        return serviceRequestRepository.findByAssignedEmployeeId(employeeId)
                .stream().map(ServiceRequestMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequestResponseDTO> getByCompanyAndStatus(
            Long companyId, RequestStatus status) {
        return serviceRequestRepository.findByCompanyIdAndStatus(companyId, status)
                .stream().map(ServiceRequestMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ServiceRequestResponseDTO updateStatus(Long id, ServiceRequestStatusDTO dto) {

        ServiceRequest sr = serviceRequestRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "ServiceRequest not found with id: " + id));

        RequestStatus newStatus;
        try {
            newStatus = RequestStatus.valueOf(dto.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + dto.getStatus());
        }

        sr.setStatus(newStatus);

        // State machine side-effects
        if (newStatus == RequestStatus.ASSIGNED && dto.getAssignedEmployeeId() != null) {
            Employee emp = employeeRepository.findById(dto.getAssignedEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            sr.setAssignedEmployee(emp);
            sr.setAssignedAt(LocalDateTime.now());
        }

        if (newStatus == RequestStatus.COMPLETED) {
            sr.setCompletedAt(LocalDateTime.now());
        }

        ServiceRequest saved = serviceRequestRepository.save(sr);
        return ServiceRequestMapper.toDTO(
                serviceRequestRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Transactional
    @Override
    public ServiceRequestResponseDTO update(Long id, ServiceRequestDTO dto) {

        ServiceRequest sr = serviceRequestRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "ServiceRequest not found with id: " + id));

        if (dto.getTitle() != null)       sr.setTitle(dto.getTitle());
        if (dto.getDescription() != null) sr.setDescription(dto.getDescription());
        if (dto.getPriority() != null)    sr.setPriority(dto.getPriority());
        if (dto.getDeadline() != null)    sr.setDeadline(dto.getDeadline());
        if (dto.getAgreedPrice() != null) sr.setAgreedPrice(dto.getAgreedPrice());
        if (dto.getSlaHours() != null)    sr.setSlaHours(dto.getSlaHours());

        if (dto.getAssignedEmployeeId() != null) {
            Employee emp = employeeRepository.findById(dto.getAssignedEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            sr.setAssignedEmployee(emp);
            if (sr.getAssignedAt() == null) sr.setAssignedAt(LocalDateTime.now());
        }

        ServiceRequest saved = serviceRequestRepository.save(sr);
        return ServiceRequestMapper.toDTO(
                serviceRequestRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    public void delete(Long id) {
        serviceRequestRepository.deleteById(id);
    }
}
