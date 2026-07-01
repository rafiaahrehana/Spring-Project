package com.startuphub.controller;

import com.startuphub.dto.request.TimesheetRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.TimesheetResponse;
import com.startuphub.service.TimesheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hr/timesheets")
@RequiredArgsConstructor
@Tag(name = "Timesheets", description = "Daily work hour logging and approval")
public class TimesheetController {

    private final TimesheetService timesheetService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Log hours for a work day")
    public ResponseEntity<ApiResponse<TimesheetResponse>> log(
            @Valid @RequestBody TimesheetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Timesheet logged", timesheetService.log(request)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "List own timesheets")
    public ResponseEntity<ApiResponse<Page<TimesheetResponse>>> listMine(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "31") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            timesheetService.listMine(
                PageRequest.of(page, size, Sort.by("workDate").descending()))));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "List timesheets for an employee")
    public ResponseEntity<ApiResponse<Page<TimesheetResponse>>> listForEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "31") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            timesheetService.listForEmployee(employeeId,
                PageRequest.of(page, size, Sort.by("workDate").descending()))));
    }

    @GetMapping("/employee/{employeeId}/range")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "List timesheets for date range")
    public ResponseEntity<ApiResponse<List<TimesheetResponse>>> listByRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(
            timesheetService.listByDateRange(employeeId, from, to)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get timesheet by ID")
    public ResponseEntity<ApiResponse<TimesheetResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(timesheetService.getById(id)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Update an unapproved timesheet")
    public ResponseEntity<ApiResponse<TimesheetResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TimesheetRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Timesheet updated",
            timesheetService.update(id, request)));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Approve a timesheet")
    public ResponseEntity<ApiResponse<TimesheetResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Timesheet approved",
            timesheetService.approve(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Delete an unapproved timesheet")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        timesheetService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Timesheet deleted"));
    }
}
