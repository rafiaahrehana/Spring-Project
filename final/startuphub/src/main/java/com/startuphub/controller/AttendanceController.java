package com.startuphub.controller;

import com.startuphub.dto.request.AttendanceRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.AttendanceResponse;
import com.startuphub.service.AttendanceService;
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
@RequestMapping("/api/hr/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Employee attendance tracking")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Mark attendance for an employee")
    public ResponseEntity<ApiResponse<AttendanceResponse>> mark(
            @PathVariable Long employeeId,
            @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Attendance marked", attendanceService.mark(employeeId, request)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Update an attendance record")
    public ResponseEntity<ApiResponse<AttendanceResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Attendance updated", attendanceService.update(id, request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Get attendance record by ID")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getById(id)));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "List attendance for an employee (paginated)")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> list(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "31") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            attendanceService.list(employeeId, PageRequest.of(page, size, Sort.by("date").descending()))));
    }

    @GetMapping("/employee/{employeeId}/range")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "List attendance for date range")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> listByRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.listByDateRange(employeeId, from, to)));
    }
}
