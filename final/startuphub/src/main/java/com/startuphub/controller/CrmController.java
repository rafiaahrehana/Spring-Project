package com.startuphub.controller;

import com.startuphub.dto.request.ClientNoteRequest;
import com.startuphub.dto.request.LeadActivityRequest;
import com.startuphub.dto.request.LeadRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.ClientNoteResponse;
import com.startuphub.dto.response.LeadActivityResponse;
import com.startuphub.dto.response.LeadResponse;
import com.startuphub.enums.LeadStatus;
import com.startuphub.service.CrmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crm")
@RequiredArgsConstructor
@Tag(name = "CRM", description = "Lead management and client relationship tools")
public class CrmController {

    private final CrmService crmService;

    // ── Leads ─────────────────────────────────────────────────────

    @PostMapping("/leads")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "Create a new lead")
    public ResponseEntity<ApiResponse<LeadResponse>> createLead(
            @Valid @RequestBody LeadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Lead created", crmService.createLead(request)));
    }

    @GetMapping("/leads")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "List all leads with optional status filter")
    public ResponseEntity<ApiResponse<Page<LeadResponse>>> listLeads(
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            crmService.listLeads(status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/leads/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "List leads assigned to the current employee")
    public ResponseEntity<ApiResponse<Page<LeadResponse>>> listMyLeads(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            crmService.listMyLeads(PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/leads/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "Get lead by ID")
    public ResponseEntity<ApiResponse<LeadResponse>> getLead(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(crmService.getLeadById(id)));
    }

    @PatchMapping("/leads/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "Update lead")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLead(
            @PathVariable Long id,
            @Valid @RequestBody LeadRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Lead updated", crmService.updateLead(id, request)));
    }

    @PatchMapping("/leads/{id}/convert")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Convert lead to client — creates User + Client records")
    public ResponseEntity<ApiResponse<LeadResponse>> convertLead(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Lead converted to client",
            crmService.convertLead(id)));
    }

    @DeleteMapping("/leads/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Delete lead")
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable Long id) {
        crmService.deleteLead(id);
        return ResponseEntity.ok(ApiResponse.success("Lead deleted"));
    }

    // ── Lead Activities ───────────────────────────────────────────

    @PostMapping("/leads/{leadId}/activities")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "Log an activity against a lead")
    public ResponseEntity<ApiResponse<LeadActivityResponse>> addActivity(
            @PathVariable Long leadId,
            @Valid @RequestBody LeadActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Activity logged",
                crmService.addActivity(leadId, request)));
    }

    @GetMapping("/leads/{leadId}/activities")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "List activities for a lead")
    public ResponseEntity<ApiResponse<Page<LeadActivityResponse>>> getActivities(
            @PathVariable Long leadId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            crmService.getActivities(leadId, PageRequest.of(page, size))));
    }

    @DeleteMapping("/leads/{leadId}/activities/{activityId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Delete a lead activity")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(
            @PathVariable Long leadId,
            @PathVariable Long activityId) {
        crmService.deleteActivity(leadId, activityId);
        return ResponseEntity.ok(ApiResponse.success("Activity deleted"));
    }

    // ── Client Notes ──────────────────────────────────────────────

    @PostMapping("/clients/{clientId}/notes")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "Add a note to a client")
    public ResponseEntity<ApiResponse<ClientNoteResponse>> addNote(
            @PathVariable Long clientId,
            @Valid @RequestBody ClientNoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Note added",
                crmService.addClientNote(clientId, request)));
    }

    @GetMapping("/clients/{clientId}/notes")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN','EMPLOYEE')")
    @Operation(summary = "List notes for a client")
    public ResponseEntity<ApiResponse<Page<ClientNoteResponse>>> getNotes(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            crmService.getClientNotes(clientId, PageRequest.of(page, size))));
    }

    @DeleteMapping("/clients/{clientId}/notes/{noteId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Delete a client note")
    public ResponseEntity<ApiResponse<Void>> deleteNote(
            @PathVariable Long clientId,
            @PathVariable Long noteId) {
        crmService.deleteClientNote(clientId, noteId);
        return ResponseEntity.ok(ApiResponse.success("Note deleted"));
    }
}
