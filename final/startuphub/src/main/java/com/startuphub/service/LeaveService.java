package com.startuphub.service;

import com.startuphub.dto.request.LeaveRequestDto;
import com.startuphub.dto.request.ReviewLeaveRequest;
import com.startuphub.dto.response.LeaveBalanceResponse;
import com.startuphub.dto.response.LeaveRequestResponse;
import com.startuphub.enums.LeaveRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeaveService {
    LeaveRequestResponse apply(LeaveRequestDto request);
    LeaveRequestResponse getById(Long id);
    Page<LeaveRequestResponse> listAll(LeaveRequestStatus status, Pageable pageable);
    Page<LeaveRequestResponse> listMyLeaves(Pageable pageable);
    LeaveRequestResponse review(Long id, ReviewLeaveRequest request);
    void cancel(Long id);
    List<LeaveBalanceResponse> getMyBalances(int year);
    List<LeaveBalanceResponse> getBalancesForEmployee(Long employeeId, int year);
}
