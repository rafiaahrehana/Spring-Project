package com.startuphub.mapper;

import com.startuphub.dto.response.RequestCommentResponse;
import com.startuphub.dto.response.RequestStatusHistoryResponse;
import com.startuphub.dto.response.ServiceRequestResponse;
import com.startuphub.dto.response.TaskResponse;
import com.startuphub.entity.Client;
import com.startuphub.entity.Employee;
import com.startuphub.entity.RequestComment;
import com.startuphub.entity.RequestStatusHistory;
import com.startuphub.entity.ServiceRequest;
import com.startuphub.entity.Task;
import com.startuphub.entity.User;
import com.startuphub.entity.WorkflowStage;

public final class ServiceRequestMapper {

    private ServiceRequestMapper() {}

    public static ServiceRequestResponse toResponse(
            ServiceRequest r, long taskCount, long completedTaskCount) {
        Client client = r.getClient();
        Employee assigned = r.getAssignedEmployee();
        User clientUser = client != null ? client.getUser() : null;
        User assignedUser = assigned != null ? assigned.getUser() : null;
        return new ServiceRequestResponse(
            r.getId(),
            r.getTitle(),
            r.getDescription(),
            r.getStatus(),
            r.getPriority(),
            r.getAgreedPrice(),
            r.getSlaDeadline(),
            r.isSlaBreach(),
            r.getAssignedAt(),
            r.getCompletedAt(),
            r.getResubmitCount(),
            r.isPermanentlyClosed(),
            r.getCompany() != null ? r.getCompany().getId() : null,
            client != null ? client.getId() : null,
            clientUser != null ? clientUser.getFullName() : null,
            r.getHubService() != null ? r.getHubService().getId() : null,
            r.getHubService() != null ? r.getHubService().getName() : null,
            assigned != null ? assigned.getId() : null,
            assignedUser != null ? assignedUser.getFullName() : null,
            taskCount,
            completedTaskCount,
            r.getCreatedAt(),
            r.getUpdatedAt()
        );
    }

    public static TaskResponse toTaskResponse(Task t) {
        Employee assigned = t.getAssignedEmployee();
        User createdBy = t.getCreatedBy();
        WorkflowStage stage = t.getWorkflowStage();
        return new TaskResponse(
            t.getId(),
            t.getTitle(),
            t.getDescription(),
            t.getStatus(),
            t.getPriority(),
            t.getDueDate(),
            t.getSlaDeadline(),
            t.getCompletedAt(),
            t.getServiceRequest() != null ? t.getServiceRequest().getId() : null,
            assigned != null ? assigned.getId() : null,
            assigned != null && assigned.getUser() != null
                ? assigned.getUser().getFullName() : null,
            createdBy != null ? createdBy.getId() : null,
            createdBy != null ? createdBy.getFullName() : null,
            stage != null ? stage.getId() : null,
            stage != null ? stage.getName() : null,
            t.getCreatedAt()
        );
    }

    public static RequestCommentResponse toCommentResponse(RequestComment c) {
        User author = c.getAuthor();
        return new RequestCommentResponse(
            c.getId(),
            c.getContent(),
            c.getVisibility(),
            c.getAttachmentUrl(),
            author != null ? author.getId() : null,
            author != null ? author.getFullName() : null,
            c.getCreatedAt()
        );
    }

    public static RequestStatusHistoryResponse toHistoryResponse(RequestStatusHistory h) {
        User changedBy = h.getChangedBy();
        return new RequestStatusHistoryResponse(
            h.getId(),
            h.getOldStatus(),
            h.getNewStatus(),
            h.getReason(),
            changedBy != null ? changedBy.getId() : null,
            changedBy != null ? changedBy.getFullName() : null,
            h.getChangedAt()
        );
    }
}
