package com.startuphub.service;

import com.startuphub.dto.request.TimesheetRequest;
import com.startuphub.dto.response.TimesheetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TimesheetService {
    TimesheetResponse log(TimesheetRequest request);
    TimesheetResponse getById(Long id);
    Page<TimesheetResponse> listMine(Pageable pageable);
    Page<TimesheetResponse> listForEmployee(Long employeeId, Pageable pageable);
    List<TimesheetResponse> listByDateRange(Long employeeId, LocalDate from, LocalDate to);
    TimesheetResponse update(Long id, TimesheetRequest request);
    TimesheetResponse approve(Long id);
    void delete(Long id);
}
