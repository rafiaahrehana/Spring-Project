package com.saas.luminex.service.impl;

import com.saas.luminex.dto.mapper.ServiceRequestMapper;
import com.saas.luminex.dto.request.ServiceRequestCreateRequest;
import com.saas.luminex.dto.request.ServiceRequestUpdateRequest;
import com.saas.luminex.dto.response.ServiceRequestResponse;
import com.saas.luminex.entity.Service;
import com.saas.luminex.entity.ServiceRequest;
import com.saas.luminex.entity.User;
import com.saas.luminex.enums.NotificationType;
import com.saas.luminex.enums.Role;
import com.saas.luminex.exception.BadRequestException;
import com.saas.luminex.exception.ResourceNotFoundException;
import com.saas.luminex.repository.ServiceRepository;
import com.saas.luminex.repository.ServiceRequestRepository;
import com.saas.luminex.repository.UserRepository;
import com.saas.luminex.service.NotificationService;
import com.saas.luminex.service.ServiceRequestService;
import com.saas.luminex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestRepository requestRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public ServiceRequestResponse createRequest(ServiceRequestCreateRequest dto) {
        User client = securityUtil.getCurrentUser();
        Service service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", dto.getServiceId()));

        ServiceRequest request = ServiceRequest.builder()
                .client(client)
                .service(service)
                .priority(dto.getPriority())
                .clientNotes(dto.getClientNotes())
                .build();

        ServiceRequest saved = requestRepository.save(request);

        notificationService.send(client,
                "Request Submitted",
                "Your request for \"" + service.getName() + "\" has been received.",
                NotificationType.REQUEST_SUBMITTED);

        return ServiceRequestMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceRequestResponse getRequestById(Long id) {
        return ServiceRequestMapper.toDTO(findRequest(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceRequestResponse> getAllRequests(Pageable pageable) {
        return requestRepository.findAll(pageable).map(ServiceRequestMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceRequestResponse> getMyRequests(Pageable pageable) {
        User client = securityUtil.getCurrentUser();
        return requestRepository.findByClient(client, pageable).map(ServiceRequestMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceRequestResponse> getMyAssignedTasks(Pageable pageable) {
        User employee = securityUtil.getCurrentUser();
        return requestRepository.findByAssignedEmployee(employee, pageable).map(ServiceRequestMapper::toDTO);
    }

    @Override
    @Transactional
    public ServiceRequestResponse updateRequest(Long id, ServiceRequestUpdateRequest dto) {
        ServiceRequest request = findRequest(id);

        if (dto.getStatus() != null)    request.setStatus(dto.getStatus());
        if (dto.getPriority() != null)  request.setPriority(dto.getPriority());
        if (dto.getProgress() != null) {
            if (dto.getProgress() < 0 || dto.getProgress() > 100) {
                throw new BadRequestException("Progress must be between 0 and 100");
            }
            request.setProgress(dto.getProgress());
        }
        if (dto.getWorkedHours() != null) request.setWorkedHours(dto.getWorkedHours());
        if (dto.getAdminNotes() != null)  request.setAdminNotes(dto.getAdminNotes());

        if (dto.getAssignedEmployeeId() != null) {
            User employee = userRepository.findById(dto.getAssignedEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", dto.getAssignedEmployeeId()));
            if (employee.getRole() != Role.EMPLOYEE) {
                throw new BadRequestException("Assigned user must have EMPLOYEE role");
            }
            request.setAssignedEmployee(employee);

            notificationService.send(employee,
                    "New Task Assigned",
                    "You have been assigned to: \"" + request.getService().getName() + "\"",
                    NotificationType.REQUEST_ASSIGNED);
        }

        if (dto.getStatus() != null && dto.getStatus().name().equals("COMPLETED")) {
            notificationService.send(request.getClient(),
                    "Request Completed",
                    "Your request for \"" + request.getService().getName() + "\" is complete. Please proceed to payment.",
                    NotificationType.PAYMENT_DUE);
        }

        return ServiceRequestMapper.toDTO(requestRepository.save(request));
    }

    @Override
    @Transactional
    public void deleteRequest(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new ResourceNotFoundException("ServiceRequest", id);
        }
        requestRepository.deleteById(id);
    }

    private ServiceRequest findRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRequest", id));
    }
}
