package com.startuphub.service.impl;

import com.startuphub.dto.request.AddCommentRequest;
import com.startuphub.dto.request.ChangeRequestStatusRequest;
import com.startuphub.dto.request.CreateNotificationRequest;
import com.startuphub.dto.request.CreateServiceRequestRequest;
import com.startuphub.dto.request.CreateTaskRequest;
import com.startuphub.dto.request.UpdateServiceRequestRequest;
import com.startuphub.dto.request.UpdateTaskRequest;
import com.startuphub.dto.response.RequestCommentResponse;
import com.startuphub.dto.response.RequestStatusHistoryResponse;
import com.startuphub.dto.response.ServiceRequestResponse;
import com.startuphub.dto.response.TaskResponse;
import com.startuphub.entity.Client;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.entity.HubService;
import com.startuphub.entity.RequestComment;
import com.startuphub.entity.RequestStatusHistory;
import com.startuphub.entity.ServiceRequest;
import com.startuphub.entity.Task;
import com.startuphub.entity.User;
import com.startuphub.entity.WorkflowStage;
import com.startuphub.enums.CommentVisibility;
import com.startuphub.enums.NotificationType;
import com.startuphub.enums.ServiceRequestPriority;
import com.startuphub.enums.ServiceRequestStatus;
import com.startuphub.enums.TaskStatus;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.ServiceRequestMapper;
import com.startuphub.repository.ClientRepository;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.HubServiceRepository;
import com.startuphub.repository.RequestCommentRepository;
import com.startuphub.repository.RequestStatusHistoryRepository;
import com.startuphub.repository.ServiceRequestRepository;
import com.startuphub.repository.TaskRepository;
import com.startuphub.repository.WorkflowStageRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.NotificationService;
import com.startuphub.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestRepository       serviceRequestRepository;
    private final TaskRepository                 taskRepository;
    private final RequestCommentRepository       commentRepository;
    private final RequestStatusHistoryRepository historyRepository;
    private final HubServiceRepository           hubServiceRepository;
    private final ClientRepository               clientRepository;
    private final EmployeeRepository             employeeRepository;
    private final WorkflowStageRepository        workflowStageRepository;
    private final NotificationService            notificationService;
    private final SecurityUtil                   securityUtil;

    // ── Service Request CRUD ──────────────────────────────────────

    @Override
    @Transactional
    public ServiceRequestResponse create(CreateServiceRequestRequest request) {
        Long companyId = requireCompanyId();
        User currentUser = securityUtil.getCurrentUser();

        Client client = clientRepository.findByUserId(currentUser.getId())
            .orElseThrow(() -> new BadRequestException(
                "Only clients can submit service requests"));

        HubService service = hubServiceRepository
            .findByIdAndCompanyId(request.hubServiceId(), companyId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Service not found: " + request.hubServiceId()));

        if (!service.isActive()) {
            throw new BadRequestException("This service is currently unavailable");
        }

        ServiceRequest sr = ServiceRequest.builder()
            .title(request.title())
            .description(request.description())
            .status(ServiceRequestStatus.PENDING)
            .priority(request.priority() != null ? request.priority() : service.getDefaultPriority())
            .agreedPrice(request.agreedPrice() != null ? request.agreedPrice() : service.getPrice())
            .slaDeadline(request.slaDeadline())
            .company(companyRef(companyId))
            .client(client)
            .hubService(service)
            .build();

        serviceRequestRepository.save(sr);
        recordStatusChange(sr, null, ServiceRequestStatus.PENDING, "Request submitted", currentUser, companyId);

        log.info("ServiceRequest created: id={} company={} client={}", sr.getId(), companyId, client.getId());
        return toResponse(sr);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceRequestResponse getById(Long id) {
        return toResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceRequestResponse> listAll(ServiceRequestStatus status, Pageable pageable) {
        Long companyId = requireCompanyId();
        Page<ServiceRequest> page = status != null
            ? serviceRequestRepository.findByCompanyIdAndStatus(companyId, status, pageable)
            : serviceRequestRepository.findByCompanyId(companyId, pageable);
        return page.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceRequestResponse> listMyRequests(Pageable pageable) {
        Long companyId = requireCompanyId();
        User currentUser = securityUtil.getCurrentUser();
        Client client = clientRepository.findByUserId(currentUser.getId())
            .orElseThrow(() -> new BadRequestException("Client profile not found"));
        return serviceRequestRepository
            .findByCompanyIdAndClientId(companyId, client.getId(), pageable)
            .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceRequestResponse> listAssignedToMe(Pageable pageable) {
        Long companyId = requireCompanyId();
        User currentUser = securityUtil.getCurrentUser();
        Employee emp = employeeRepository.findByUserId(currentUser.getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));
        return serviceRequestRepository
            .findByCompanyIdAndAssignedEmployeeId(companyId, emp.getId(), pageable)
            .map(this::toResponse);
    }

    @Override
    @Transactional
    public ServiceRequestResponse update(Long id, UpdateServiceRequestRequest request) {
        ServiceRequest sr = findInTenant(id);
        guardNotClosed(sr);

        if (request.title()              != null) sr.setTitle(request.title());
        if (request.description()        != null) sr.setDescription(request.description());
        if (request.priority()           != null) sr.setPriority(request.priority());
        if (request.agreedPrice()        != null) sr.setAgreedPrice(request.agreedPrice());
        if (request.slaDeadline()        != null) sr.setSlaDeadline(request.slaDeadline());

        if (request.assignedEmployeeId() != null) {
            Employee emp = employeeRepository
                .findByIdAndCompanyId(request.assignedEmployeeId(), requireCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Employee not found: " + request.assignedEmployeeId()));
            sr.setAssignedEmployee(emp);
            if (sr.getAssignedAt() == null) {
                sr.setAssignedAt(LocalDateTime.now());
            }
        }
        return toResponse(sr);
    }

    @Override
    @Transactional
    public ServiceRequestResponse changeStatus(Long id, ChangeRequestStatusRequest request) {
        ServiceRequest sr = findInTenant(id);
        guardNotClosed(sr);

        ServiceRequestStatus oldStatus = sr.getStatus();
        ServiceRequestStatus newStatus = request.status();
        User currentUser = securityUtil.getCurrentUser();

        sr.setStatus(newStatus);
        if (newStatus == ServiceRequestStatus.COMPLETED) {
            sr.setCompletedAt(LocalDateTime.now());
        }
        if (newStatus == ServiceRequestStatus.ASSIGNED && sr.getAssignedAt() == null) {
            sr.setAssignedAt(LocalDateTime.now());
        }

        recordStatusChange(sr, oldStatus, newStatus, request.reason(), currentUser, requireCompanyId());

        // Notify client on key status changes
        notifyClientOnStatusChange(sr, newStatus);

        log.info("ServiceRequest status changed: id={} {} → {}", id, oldStatus, newStatus);
        return toResponse(sr);
    }

    @Override
    @Transactional
    public ServiceRequestResponse assign(Long id, Long employeeId) {
        Long companyId = requireCompanyId();
        ServiceRequest sr = findInTenant(id);
        guardNotClosed(sr);

        Employee emp = employeeRepository.findByIdAndCompanyId(employeeId, companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        ServiceRequestStatus old = sr.getStatus();
        sr.setAssignedEmployee(emp);
        sr.setAssignedAt(LocalDateTime.now());
        sr.setStatus(ServiceRequestStatus.ASSIGNED);

        recordStatusChange(sr, old, ServiceRequestStatus.ASSIGNED,
            "Assigned to " + emp.getUser().getFullName(),
            securityUtil.getCurrentUser(), companyId);

        // Notify the assigned employee
        if (emp.getUser() != null) {
            notificationService.sendForServiceRequest(CreateNotificationRequest.forRequest(
                NotificationType.REQUEST_ASSIGNED,
                "Request Assigned",
                "Service request \"" + sr.getTitle() + "\" has been assigned to you.",
                emp.getUser().getId(), companyId, sr.getId()
            ));
        }

        return toResponse(sr);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        ServiceRequest sr = findInTenant(id);
        if (sr.getStatus() == ServiceRequestStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed request");
        }
        ServiceRequestStatus oldStatus = sr.getStatus();
        sr.setStatus(ServiceRequestStatus.CANCELLED);
        sr.setPermanentlyClosed(true);
        recordStatusChange(sr, oldStatus, ServiceRequestStatus.CANCELLED,
            "Cancelled by user", securityUtil.getCurrentUser(), requireCompanyId());
    }

    // ── Tasks ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public TaskResponse addTask(Long requestId, CreateTaskRequest request) {
        Long companyId = requireCompanyId();
        ServiceRequest sr = findInTenant(requestId);
        User currentUser = securityUtil.getCurrentUser();

        Task task = Task.builder()
            .title(request.title())
            .description(request.description())
            .priority(request.priority() != null ? request.priority() : ServiceRequestPriority.NORMAL)
            .dueDate(request.dueDate())
            .slaDeadline(request.slaDeadline())
            .serviceRequest(sr)
            .company(companyRef(companyId))
            .createdBy(currentUser)
            .build();

        if (request.assignedEmployeeId() != null) {
            Employee emp = employeeRepository.findByIdAndCompanyId(request.assignedEmployeeId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Employee not found: " + request.assignedEmployeeId()));
            task.setAssignedEmployee(emp);
            // Notify the assigned employee
            if (emp.getUser() != null) {
                notificationService.sendForServiceRequest(CreateNotificationRequest.forRequest(
                    NotificationType.REQUEST_ASSIGNED,
                    "Task Assigned",
                    "A new task \"" + task.getTitle() + "\" has been assigned to you.",
                    emp.getUser().getId(), companyId, requestId
                ));
            }
        }
        if (request.workflowStageId() != null) {
            task.setWorkflowStage(
                workflowStageRepository.findByIdAndCompanyId(request.workflowStageId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Workflow stage not found: " + request.workflowStageId())));
        }

        taskRepository.save(task);
        return ServiceRequestMapper.toTaskResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(Long requestId) {
        findInTenant(requestId);
        return taskRepository.findByServiceRequestIdOrderByCreatedAtAsc(requestId)
            .stream().map(ServiceRequestMapper::toTaskResponse).toList();
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long requestId, Long taskId, UpdateTaskRequest request) {
        Long companyId = requireCompanyId();
        findInTenant(requestId);
        Task task = taskRepository.findByIdAndCompanyId(taskId, companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        if (request.title()       != null) task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.priority()    != null) task.setPriority(request.priority());
        if (request.dueDate()     != null) task.setDueDate(request.dueDate());
        if (request.slaDeadline() != null) task.setSlaDeadline(request.slaDeadline());
        if (request.status()      != null) {
            task.setStatus(request.status());
            if (request.status() == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
                task.setCompletedAt(LocalDateTime.now());
            }
        }
        if (request.assignedEmployeeId() != null) {
            task.setAssignedEmployee(
                employeeRepository.findByIdAndCompanyId(request.assignedEmployeeId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found: " + request.assignedEmployeeId())));
        }
        return ServiceRequestMapper.toTaskResponse(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long requestId, Long taskId) {
        findInTenant(requestId);
        Task task = taskRepository.findByIdAndCompanyId(taskId, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        task.softDelete();
    }

    // ── Comments ──────────────────────────────────────────────────

    @Override
    @Transactional
    public RequestCommentResponse addComment(Long requestId, AddCommentRequest request) {
        Long companyId = requireCompanyId();
        ServiceRequest sr = findInTenant(requestId);
        User currentUser = securityUtil.getCurrentUser();

        RequestComment comment = RequestComment.builder()
            .content(request.content())
            .visibility(request.visibility() != null
                ? request.visibility() : CommentVisibility.INTERNAL)
            .attachmentUrl(request.attachmentUrl())
            .serviceRequest(sr)
            .company(companyRef(companyId))
            .author(currentUser)
            .build();

        commentRepository.save(comment);

        // Notify client when a CLIENT-visible comment is added by staff
        if (comment.getVisibility() == CommentVisibility.CLIENT
                && sr.getClient() != null
                && sr.getClient().getUser() != null) {
            notificationService.sendForServiceRequest(CreateNotificationRequest.forRequest(
                NotificationType.REQUEST_UPDATED,
                "New Comment",
                "A new update has been added to your request \"" + sr.getTitle() + "\".",
                sr.getClient().getUser().getId(), companyId, requestId
            ));
        }

        return ServiceRequestMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RequestCommentResponse> getComments(Long requestId, Pageable pageable) {
        findInTenant(requestId);
        return commentRepository
            .findByServiceRequestIdOrderByCreatedAtDesc(requestId, pageable)
            .map(ServiceRequestMapper::toCommentResponse);
    }

    // ── Status history ────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<RequestStatusHistoryResponse> getStatusHistory(Long requestId) {
        findInTenant(requestId);
        return historyRepository
            .findByServiceRequestIdOrderByChangedAtAsc(requestId)
            .stream().map(ServiceRequestMapper::toHistoryResponse).toList();
    }

    // ── Private helpers ───────────────────────────────────────────

    private ServiceRequest findInTenant(Long id) {
        return serviceRequestRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Service request not found: " + id));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }

    private Company companyRef(Long companyId) {
        Company c = new Company();
        c.setId(companyId);
        return c;
    }

    private void guardNotClosed(ServiceRequest sr) {
        if (sr.isPermanentlyClosed()) {
            throw new BadRequestException("This request is permanently closed");
        }
    }

    private void recordStatusChange(ServiceRequest sr, ServiceRequestStatus oldStatus,
                                     ServiceRequestStatus newStatus, String reason,
                                     User changedBy, Long companyId) {
        RequestStatusHistory history = RequestStatusHistory.builder()
            .serviceRequest(sr)
            .oldStatus(oldStatus)
            .newStatus(newStatus)
            .reason(reason)
            .changedBy(changedBy)
            .companyId(companyId)
            .build();
        historyRepository.save(history);
    }

    private void notifyClientOnStatusChange(ServiceRequest sr, ServiceRequestStatus newStatus) {
        if (sr.getClient() == null || sr.getClient().getUser() == null) return;
        NotificationType type = switch (newStatus) {
            case COMPLETED -> NotificationType.COMPLETED;
            case REJECTED  -> NotificationType.REJECTED;
            case CANCELLED -> NotificationType.CANCELLED;
            case IN_PROGRESS -> NotificationType.REQUEST_UPDATED;
            case WAITING_CLIENT -> NotificationType.REQUEST_UPDATED;
            default -> null;
        };
        if (type == null) return;
        String message = "Your request \"" + sr.getTitle() + "\" is now " + newStatus.name().replace('_', ' ') + ".";
        notificationService.sendForServiceRequest(CreateNotificationRequest.forRequest(
            type, "Request Update", message,
            sr.getClient().getUser().getId(),
            sr.getCompany().getId(),
            sr.getId()
        ));
    }

    private ServiceRequestResponse toResponse(ServiceRequest sr) {
        long taskCount = taskRepository.countByServiceRequestId(sr.getId());
        long completedCount = taskRepository.countByServiceRequestIdAndStatus(
            sr.getId(), TaskStatus.COMPLETED);
        return ServiceRequestMapper.toResponse(sr, taskCount, completedCount);
    }
}
