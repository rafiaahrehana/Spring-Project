package com.startuphub.service;

import com.startuphub.dto.request.ClientNoteRequest;
import com.startuphub.dto.request.LeadActivityRequest;
import com.startuphub.dto.request.LeadRequest;
import com.startuphub.dto.response.ClientNoteResponse;
import com.startuphub.dto.response.LeadActivityResponse;
import com.startuphub.dto.response.LeadResponse;
import com.startuphub.enums.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrmService {

    // ── Leads ─────────────────────────────────────────────────────

    LeadResponse createLead(LeadRequest request);

    LeadResponse getLeadById(Long id);

    Page<LeadResponse> listLeads(LeadStatus status, Pageable pageable);

    Page<LeadResponse> listMyLeads(Pageable pageable);

    LeadResponse updateLead(Long id, LeadRequest request);

    LeadResponse convertLead(Long id);

    void deleteLead(Long id);

    // ── Lead Activities ───────────────────────────────────────────

    LeadActivityResponse addActivity(Long leadId, LeadActivityRequest request);

    Page<LeadActivityResponse> getActivities(Long leadId, Pageable pageable);

    void deleteActivity(Long leadId, Long activityId);

    // ── Client Notes ──────────────────────────────────────────────

    ClientNoteResponse addClientNote(Long clientId, ClientNoteRequest request);

    Page<ClientNoteResponse> getClientNotes(Long clientId, Pageable pageable);

    void deleteClientNote(Long clientId, Long noteId);
}
