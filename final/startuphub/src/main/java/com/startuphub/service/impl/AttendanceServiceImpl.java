package com.startuphub.service.impl;

import com.startuphub.dto.request.AttendanceRequest;
import com.startuphub.dto.response.AttendanceResponse;
import com.startuphub.entity.Attendance;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.HrmMapper;
import com.startuphub.repository.AttendanceRepository;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository   employeeRepository;
    private final SecurityUtil         securityUtil;

    @Override
    @Transactional
    public AttendanceResponse mark(Long employeeId, AttendanceRequest request) {
        Long companyId = requireCompanyId();
        Employee employee = findEmployee(employeeId, companyId);

        if (attendanceRepository.findByEmployeeIdAndDate(employeeId, request.date()).isPresent()) {
            throw new BadRequestException("Attendance already marked for "
                + employee.getUser().getFullName() + " on " + request.date());
        }

        Attendance a = Attendance.builder()
            .employee(employee)
            .company(companyRef(companyId))
            .date(request.date())
            .checkIn(request.checkIn())
            .checkOut(request.checkOut())
            .present(request.present())
            .notes(request.notes())
            .build();
        attendanceRepository.save(a);
        return HrmMapper.toAttendanceResponse(a);
    }

    @Override
    @Transactional
    public AttendanceResponse update(Long id, AttendanceRequest request) {
        Attendance a = findInTenant(id);
        a.setCheckIn(request.checkIn());
        a.setCheckOut(request.checkOut());
        a.setPresent(request.present());
        if (request.notes() != null) a.setNotes(request.notes());
        return HrmMapper.toAttendanceResponse(a);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> list(Long employeeId, Pageable pageable) {
        Long companyId = requireCompanyId();
        return attendanceRepository.findByCompanyIdAndEmployeeId(companyId, employeeId, pageable)
            .map(HrmMapper::toAttendanceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> listByDateRange(Long employeeId, LocalDate from, LocalDate to) {
        Long companyId = requireCompanyId();
        return attendanceRepository.findByCompanyIdAndEmployeeIdAndDateBetween(
                companyId, employeeId, from, to)
            .stream().map(HrmMapper::toAttendanceResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getById(Long id) {
        return HrmMapper.toAttendanceResponse(findInTenant(id));
    }

    private Attendance findInTenant(Long id) {
        return attendanceRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found: " + id));
    }

    private Employee findEmployee(Long employeeId, Long companyId) {
        return employeeRepository.findByIdAndCompanyId(employeeId, companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }

    private Company companyRef(Long companyId) {
        Company c = new Company(); c.setId(companyId); return c;
    }
}
