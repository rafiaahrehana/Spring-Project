package com.startuphub.service;

import com.startuphub.dto.request.AttendanceRequest;
import com.startuphub.dto.response.AttendanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponse mark(Long employeeId, AttendanceRequest request);
    AttendanceResponse update(Long id, AttendanceRequest request);
    Page<AttendanceResponse> list(Long employeeId, Pageable pageable);
    List<AttendanceResponse> listByDateRange(Long employeeId, LocalDate from, LocalDate to);
    AttendanceResponse getById(Long id);
}
