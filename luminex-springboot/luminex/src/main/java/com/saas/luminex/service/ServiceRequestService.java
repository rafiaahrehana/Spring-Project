package com.saas.luminex.service;

import com.saas.luminex.dto.request.ServiceRequestCreateRequest;
import com.saas.luminex.dto.request.ServiceRequestUpdateRequest;
import com.saas.luminex.dto.response.ServiceRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceRequestService {
    ServiceRequestResponse createRequest(ServiceRequestCreateRequest request);
    ServiceRequestResponse getRequestById(Long id);
    Page<ServiceRequestResponse> getAllRequests(Pageable pageable);
    Page<ServiceRequestResponse> getMyRequests(Pageable pageable);         // CLIENT
    Page<ServiceRequestResponse> getMyAssignedTasks(Pageable pageable);    // EMPLOYEE
    ServiceRequestResponse updateRequest(Long id, ServiceRequestUpdateRequest update);
    void deleteRequest(Long id);
}
