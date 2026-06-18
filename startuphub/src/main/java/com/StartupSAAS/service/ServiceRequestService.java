package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.ServiceRequestDTO;
import com.StartupSAAS.dto.request.ServiceRequestStatusDTO;
import com.StartupSAAS.dto.response.ServiceRequestResponseDTO;
import com.StartupSAAS.enums.RequestStatus;

import java.util.List;

public interface ServiceRequestService {

    ServiceRequestResponseDTO create(ServiceRequestDTO dto);
    List<ServiceRequestResponseDTO> getAll();
    ServiceRequestResponseDTO getById(Long id);
    List<ServiceRequestResponseDTO> getByCompany(Long companyId);
    List<ServiceRequestResponseDTO> getByClient(Long clientId);
    List<ServiceRequestResponseDTO> getByEmployee(Long employeeId);
    List<ServiceRequestResponseDTO> getByCompanyAndStatus(Long companyId, RequestStatus status);
    ServiceRequestResponseDTO updateStatus(Long id, ServiceRequestStatusDTO dto);
    ServiceRequestResponseDTO update(Long id, ServiceRequestDTO dto);
    void delete(Long id);
}
