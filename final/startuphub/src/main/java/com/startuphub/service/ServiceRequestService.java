package com.startuphub.service;

import com.startuphub.dto.request.AddCommentRequest;
import com.startuphub.dto.request.ChangeRequestStatusRequest;
import com.startuphub.dto.request.CreateServiceRequestRequest;
import com.startuphub.dto.request.CreateTaskRequest;
import com.startuphub.dto.request.UpdateServiceRequestRequest;
import com.startuphub.dto.request.UpdateTaskRequest;
import com.startuphub.dto.response.RequestCommentResponse;
import com.startuphub.dto.response.RequestStatusHistoryResponse;
import com.startuphub.dto.response.ServiceRequestResponse;
import com.startuphub.dto.response.TaskResponse;
import com.startuphub.enums.ServiceRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServiceRequestService {

    ServiceRequestResponse create(CreateServiceRequestRequest request);

    ServiceRequestResponse getById(Long id);

    Page<ServiceRequestResponse> listAll(ServiceRequestStatus status, Pageable pageable);

    Page<ServiceRequestResponse> listMyRequests(Pageable pageable);

    Page<ServiceRequestResponse> listAssignedToMe(Pageable pageable);

    ServiceRequestResponse update(Long id, UpdateServiceRequestRequest request);

    ServiceRequestResponse changeStatus(Long id, ChangeRequestStatusRequest request);

    ServiceRequestResponse assign(Long id, Long employeeId);

    void cancel(Long id);

    // Tasks
    TaskResponse addTask(Long requestId, CreateTaskRequest request);

    List<TaskResponse> getTasks(Long requestId);

    TaskResponse updateTask(Long requestId, Long taskId, UpdateTaskRequest request);

    void deleteTask(Long requestId, Long taskId);

    // Comments
    RequestCommentResponse addComment(Long requestId, AddCommentRequest request);

    Page<RequestCommentResponse> getComments(Long requestId, Pageable pageable);

    // Status history
    List<RequestStatusHistoryResponse> getStatusHistory(Long requestId);
}
