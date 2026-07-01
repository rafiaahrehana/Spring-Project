package com.startuphub.service.impl;

import com.startuphub.dto.request.LeaveRequestDto;
import com.startuphub.dto.request.ReviewLeaveRequest;
import com.startuphub.dto.response.LeaveBalanceResponse;
import com.startuphub.dto.response.LeaveRequestResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.entity.LeaveBalance;
import com.startuphub.entity.LeaveRequest;
import com.startuphub.entity.User;
import com.startuphub.enums.LeaveRequestStatus;
import com.startuphub.enums.LeaveType;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.HrmMapper;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.LeaveBalanceRepository;
import com.startuphub.repository.LeaveRequestRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.LeaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository     employeeRepository;
    private final SecurityUtil           securityUtil;

    @Override
    @Transactional
    public LeaveRequestResponse apply(LeaveRequestDto request) {
        Long companyId = requireCompanyId();
        User currentUser = securityUtil.getCurrentUser();
        Employee employee = employeeRepository.findByUserId(currentUser.getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));

        if (request.endDate().isBefore(request.startDate())) {
            throw new BadRequestException("End date must be on or after start date");
        }

        if (leaveRequestRepository.hasOverlappingLeave(
                employee.getId(), request.startDate(), request.endDate())) {
            throw new BadRequestException("You already have a leave request overlapping this period");
        }

        int totalDays = (int) ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;

        // Check leave balance
        leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(
                employee.getId(), request.leaveType(), request.startDate().getYear())
            .ifPresent(balance -> {
                if (balance.getRemainingDays() < totalDays) {
                    throw new BadRequestException("Insufficient " + request.leaveType()
                        + " leave balance. Available: " + balance.getRemainingDays()
                        + " days, Requested: " + totalDays + " days.");
                }
                balance.setPendingDays(balance.getPendingDays() + totalDays);
            });

        LeaveRequest lr = LeaveRequest.builder()
            .leaveType(request.leaveType())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .totalDays(totalDays)
            .reason(request.reason())
            .status(LeaveRequestStatus.PENDING)
            .employee(employee)
            .company(companyRef(companyId))
            .build();

        leaveRequestRepository.save(lr);
        return HrmMapper.toLeaveRequestResponse(lr);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequestResponse getById(Long id) {
        return HrmMapper.toLeaveRequestResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveRequestResponse> listAll(LeaveRequestStatus status, Pageable pageable) {
        Long companyId = requireCompanyId();
        Page<LeaveRequest> page = status != null
            ? leaveRequestRepository.findByCompanyIdAndStatus(companyId, status, pageable)
            : leaveRequestRepository.findByCompanyId(companyId, pageable);
        return page.map(HrmMapper::toLeaveRequestResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveRequestResponse> listMyLeaves(Pageable pageable) {
        Long companyId = requireCompanyId();
        Employee emp = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));
        return leaveRequestRepository.findByCompanyIdAndEmployeeId(companyId, emp.getId(), pageable)
            .map(HrmMapper::toLeaveRequestResponse);
    }

    @Override
    @Transactional
    public LeaveRequestResponse review(Long id, ReviewLeaveRequest request) {
        LeaveRequest lr = findInTenant(id);
        if (lr.getStatus() != LeaveRequestStatus.PENDING) {
            throw new BadRequestException("Only PENDING leave requests can be reviewed");
        }
        if (request.status() == LeaveRequestStatus.REJECTED
                && (request.rejectionReason() == null || request.rejectionReason().isBlank())) {
            throw new BadRequestException("Rejection reason is required when rejecting a leave request");
        }

        Employee reviewer = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));

        lr.setStatus(request.status());
        lr.setRejectionReason(request.rejectionReason());
        lr.setReviewedBy(reviewer);
        lr.setReviewedAt(LocalDateTime.now());

        // Update leave balance
        leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(
                lr.getEmployee().getId(), lr.getLeaveType(), lr.getStartDate().getYear())
            .ifPresent(balance -> {
                balance.setPendingDays(Math.max(0, balance.getPendingDays() - lr.getTotalDays()));
                if (request.status() == LeaveRequestStatus.APPROVED) {
                    balance.setUsedDays(balance.getUsedDays() + lr.getTotalDays());
                }
            });

        return HrmMapper.toLeaveRequestResponse(lr);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        LeaveRequest lr = findInTenant(id);
        if (lr.getStatus() == LeaveRequestStatus.APPROVED
                && lr.getStartDate().isBefore(java.time.LocalDate.now())) {
            throw new BadRequestException("Cannot cancel a leave that has already started");
        }
        leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(
                lr.getEmployee().getId(), lr.getLeaveType(), lr.getStartDate().getYear())
            .ifPresent(balance -> {
                if (lr.getStatus() == LeaveRequestStatus.PENDING) {
                    balance.setPendingDays(Math.max(0, balance.getPendingDays() - lr.getTotalDays()));
                } else if (lr.getStatus() == LeaveRequestStatus.APPROVED) {
                    balance.setUsedDays(Math.max(0, balance.getUsedDays() - lr.getTotalDays()));
                }
            });
        lr.setStatus(LeaveRequestStatus.CANCELLED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getMyBalances(int year) {
        Employee emp = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));
        return leaveBalanceRepository.findByEmployeeIdAndYear(emp.getId(), year)
            .stream().map(HrmMapper::toLeaveBalanceResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getBalancesForEmployee(Long employeeId, int year) {
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year)
            .stream().map(HrmMapper::toLeaveBalanceResponse).toList();
    }

    private LeaveRequest findInTenant(Long id) {
        return leaveRequestRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + id));
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
