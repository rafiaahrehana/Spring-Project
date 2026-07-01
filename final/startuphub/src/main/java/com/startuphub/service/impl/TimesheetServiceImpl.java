package com.startuphub.service.impl;

import com.startuphub.dto.request.TimesheetRequest;
import com.startuphub.dto.response.TimesheetResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.entity.Task;
import com.startuphub.entity.Timesheet;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.HrmMapper;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.TaskRepository;
import com.startuphub.repository.TimesheetRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final EmployeeRepository  employeeRepository;
    private final TaskRepository      taskRepository;
    private final SecurityUtil        securityUtil;

    @Override
    @Transactional
    public TimesheetResponse log(TimesheetRequest request) {
        Long companyId = requireCompanyId();
        Employee employee = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));

        if (timesheetRepository.findByEmployeeIdAndWorkDate(employee.getId(), request.workDate()).isPresent()) {
            throw new BadRequestException("Timesheet already logged for " + request.workDate());
        }

        Timesheet ts = Timesheet.builder()
            .employee(employee)
            .company(companyRef(companyId))
            .workDate(request.workDate())
            .startTime(request.startTime())
            .endTime(request.endTime())
            .hoursWorked(request.hoursWorked())
            .billableHours(request.billableHours() != null ? request.billableHours() : 0.0)
            .description(request.description())
            .build();

        if (request.taskId() != null) {
            Task task = taskRepository.findByIdAndCompanyId(request.taskId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + request.taskId()));
            ts.setTask(task);
        }

        timesheetRepository.save(ts);
        return HrmMapper.toTimesheetResponse(ts);
    }

    @Override
    @Transactional(readOnly = true)
    public TimesheetResponse getById(Long id) {
        return HrmMapper.toTimesheetResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TimesheetResponse> listMine(Pageable pageable) {
        Long companyId = requireCompanyId();
        Employee emp = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));
        return timesheetRepository.findByCompanyIdAndEmployeeId(companyId, emp.getId(), pageable)
            .map(HrmMapper::toTimesheetResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TimesheetResponse> listForEmployee(Long employeeId, Pageable pageable) {
        return timesheetRepository.findByCompanyIdAndEmployeeId(requireCompanyId(), employeeId, pageable)
            .map(HrmMapper::toTimesheetResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimesheetResponse> listByDateRange(Long employeeId, LocalDate from, LocalDate to) {
        return timesheetRepository.findByCompanyIdAndEmployeeIdAndWorkDateBetween(
                requireCompanyId(), employeeId, from, to)
            .stream().map(HrmMapper::toTimesheetResponse).toList();
    }

    @Override
    @Transactional
    public TimesheetResponse update(Long id, TimesheetRequest request) {
        Long companyId = requireCompanyId();
        Timesheet ts = findInTenant(id);
        if (ts.isApproved()) {
            throw new BadRequestException("Cannot edit an approved timesheet");
        }
        if (request.startTime()    != null) ts.setStartTime(request.startTime());
        if (request.endTime()      != null) ts.setEndTime(request.endTime());
        if (request.hoursWorked()  != null) ts.setHoursWorked(request.hoursWorked());
        if (request.billableHours()!= null) ts.setBillableHours(request.billableHours());
        if (request.description()  != null) ts.setDescription(request.description());
        if (request.taskId() != null) {
            ts.setTask(taskRepository.findByIdAndCompanyId(request.taskId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + request.taskId())));
        }
        return HrmMapper.toTimesheetResponse(ts);
    }

    @Override
    @Transactional
    public TimesheetResponse approve(Long id) {
        Timesheet ts = findInTenant(id);
        Employee approver = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));
        ts.setApproved(true);
        ts.setApprovedBy(approver);
        ts.setApprovedAt(LocalDateTime.now());
        return HrmMapper.toTimesheetResponse(ts);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Timesheet ts = findInTenant(id);
        if (ts.isApproved()) throw new BadRequestException("Cannot delete an approved timesheet");
        ts.softDelete();
    }

    private Timesheet findInTenant(Long id) {
        return timesheetRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found: " + id));
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
