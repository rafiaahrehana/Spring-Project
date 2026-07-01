package com.startuphub.mapper;

import com.startuphub.dto.response.AnnouncementResponse;
import com.startuphub.dto.response.ClientNoteResponse;
import com.startuphub.dto.response.EmploymentLetterResponse;
import com.startuphub.dto.response.JobApplicationResponse;
import com.startuphub.dto.response.LeadActivityResponse;
import com.startuphub.dto.response.LeadResponse;
import com.startuphub.dto.response.PerformanceReviewResponse;
import com.startuphub.dto.response.ServiceReviewResponse;
import com.startuphub.entity.Announcement;
import com.startuphub.entity.Client;
import com.startuphub.entity.ClientNote;
import com.startuphub.entity.Employee;
import com.startuphub.entity.EmploymentLetter;
import com.startuphub.entity.HubService;
import com.startuphub.entity.JobApplication;
import com.startuphub.entity.JobPosting;
import com.startuphub.entity.Lead;
import com.startuphub.entity.LeadActivity;
import com.startuphub.entity.PerformanceReview;
import com.startuphub.entity.ServiceReview;
import com.startuphub.entity.User;

public final class Phase8Mapper {

    private Phase8Mapper() {}

    public static LeadResponse toLeadResponse(Lead l) {
        Employee assigned = l.getAssignedTo();
        User assignedUser = assigned != null ? assigned.getUser() : null;
        HubService svc = l.getInterestedService();
        Client cc = l.getConvertedClient();
        return new LeadResponse(
            l.getId(), l.getContactName(), l.getCompanyName(),
            l.getEmail(), l.getPhone(), l.getIndustry(), l.getNotes(),
            l.getStatus(), l.getEstimatedValue(), l.getConvertedAt(),
            assigned != null ? assigned.getId() : null,
            assignedUser != null ? assignedUser.getFullName() : null,
            svc != null ? svc.getId() : null,
            svc != null ? svc.getName() : null,
            cc != null ? cc.getId() : null,
            l.getCreatedAt(), l.getUpdatedAt()
        );
    }

    public static LeadActivityResponse toLeadActivityResponse(LeadActivity a) {
        User createdBy = a.getCreatedBy();
        return new LeadActivityResponse(
            a.getId(), a.getActivityType(), a.getSubject(), a.getDescription(),
            a.getActivityAt(), a.getNextFollowUp(),
            a.getLead() != null ? a.getLead().getId() : null,
            createdBy != null ? createdBy.getId() : null,
            createdBy != null ? createdBy.getFullName() : null,
            a.getCreatedAt()
        );
    }

    public static ClientNoteResponse toClientNoteResponse(ClientNote n) {
        User createdBy = n.getCreatedBy();
        return new ClientNoteResponse(
            n.getId(), n.getContent(), n.getFollowUpAt(),
            n.getClient() != null ? n.getClient().getId() : null,
            createdBy != null ? createdBy.getId() : null,
            createdBy != null ? createdBy.getFullName() : null,
            n.getCreatedAt()
        );
    }

    public static JobApplicationResponse toJobApplicationResponse(JobApplication a) {
        JobPosting jp = a.getJobPosting();
        Employee reviewer = a.getReviewedBy();
        User reviewerUser = reviewer != null ? reviewer.getUser() : null;
        return new JobApplicationResponse(
            a.getId(), a.getApplicantName(), a.getApplicantEmail(),
            a.getApplicantPhone(), a.getResumeUrl(), a.getCoverLetter(),
            a.getStatus(), a.getNotes(),
            jp != null ? jp.getId() : null,
            jp != null ? jp.getTitle() : null,
            reviewer != null ? reviewer.getId() : null,
            reviewerUser != null ? reviewerUser.getFullName() : null,
            a.getCreatedAt()
        );
    }

    public static PerformanceReviewResponse toPerformanceReviewResponse(PerformanceReview pr) {
        Employee emp = pr.getEmployee();
        User empUser = emp != null ? emp.getUser() : null;
        Employee reviewer = pr.getReviewedBy();
        User reviewerUser = reviewer != null ? reviewer.getUser() : null;
        return new PerformanceReviewResponse(
            pr.getId(), pr.getReviewPeriodStart(), pr.getReviewPeriodEnd(),
            pr.getScoreWorkQuality(), pr.getScoreProductivity(), pr.getScoreCommunication(),
            pr.getScoreTeamwork(), pr.getScoreInitiative(), pr.getScorePunctuality(),
            pr.getOverallScore(), pr.getStrengths(), pr.getAreasForImprovement(),
            pr.getGoalsForNextPeriod(), pr.getComments(), pr.isFinalised(),
            emp != null ? emp.getId() : null,
            empUser != null ? empUser.getFullName() : null,
            reviewer != null ? reviewer.getId() : null,
            reviewerUser != null ? reviewerUser.getFullName() : null,
            pr.getCreatedAt()
        );
    }

    public static EmploymentLetterResponse toLetterResponse(EmploymentLetter el) {
        Employee emp = el.getEmployee();
        User empUser = emp != null ? emp.getUser() : null;
        User createdBy = el.getCreatedBy();
        return new EmploymentLetterResponse(
            el.getId(), el.getLetterType(), el.getReferenceNumber(),
            el.getIssueDate(), el.getContent(), el.getSignedBy(),
            el.getFileUrl(), el.isIssued(),
            emp != null ? emp.getId() : null,
            empUser != null ? empUser.getFullName() : null,
            createdBy != null ? createdBy.getId() : null,
            createdBy != null ? createdBy.getFullName() : null,
            el.getCreatedAt()
        );
    }

    public static AnnouncementResponse toAnnouncementResponse(Announcement a) {
        User createdBy = a.getCreatedBy();
        return new AnnouncementResponse(
            a.getId(), a.getTitle(), a.getBody(),
            a.getPublishedAt(), a.getExpiresAt(), a.isPublished(), a.isNotifyAll(),
            createdBy != null ? createdBy.getId() : null,
            createdBy != null ? createdBy.getFullName() : null,
            a.getCreatedAt()
        );
    }

    public static ServiceReviewResponse toServiceReviewResponse(ServiceReview r) {
        HubService svc = r.getHubService();
        Client client = r.getClient();
        User clientUser = client != null ? client.getUser() : null;
        return new ServiceReviewResponse(
            r.getId(), r.getRating(), r.getComment(), r.isPublished(),
            r.getServiceRequest() != null ? r.getServiceRequest().getId() : null,
            svc != null ? svc.getId() : null,
            svc != null ? svc.getName() : null,
            client != null ? client.getId() : null,
            clientUser != null ? clientUser.getFullName() : null,
            r.getCreatedAt()
        );
    }
}
