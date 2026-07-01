package com.startuphub.controller;

import com.startuphub.dto.request.AnnouncementRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.AnnouncementResponse;
import com.startuphub.service.AnnouncementService;
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

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcements", description = "Company-wide announcements and broadcasts")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Create a new announcement (starts as draft)")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(
            @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Announcement created", announcementService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "List all announcements — ADMIN view")
    public ResponseEntity<ApiResponse<Page<AnnouncementResponse>>> listAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            announcementService.listAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List active (published, non-expired) announcements")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> listActive() {
        return ResponseEntity.ok(ApiResponse.success(announcementService.listActive()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get announcement by ID")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(announcementService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Update a draft announcement")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Announcement updated",
            announcementService.update(id, request)));
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Publish announcement — notifies all employees if notifyAll=true")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> publish(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Announcement published",
            announcementService.publish(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER','ADMIN')")
    @Operation(summary = "Delete a draft announcement")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted"));
    }
}
