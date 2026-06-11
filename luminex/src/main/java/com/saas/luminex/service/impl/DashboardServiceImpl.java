package com.saas.luminex.service.impl;

import com.saas.luminex.dto.response.DashboardStatsResponse;
import com.saas.luminex.entity.User;
import com.saas.luminex.enums.RequestStatus;
import com.saas.luminex.enums.Role;
import com.saas.luminex.repository.PaymentRepository;
import com.saas.luminex.repository.ServiceRequestRepository;
import com.saas.luminex.repository.UserRepository;
import com.saas.luminex.service.DashboardService;
import com.saas.luminex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ServiceRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getAdminStats() {
        return DashboardStatsResponse.builder()
                .totalRequests(requestRepository.count())
                .pendingRequests(requestRepository.countByStatus(RequestStatus.PENDING))
                .inProgressRequests(requestRepository.countByStatus(RequestStatus.IN_PROGRESS))
                .completedRequests(requestRepository.countByStatus(RequestStatus.COMPLETED))
                .totalClients(userRepository.countActiveByRole(Role.CLIENT))
                .totalEmployees(userRepository.countActiveByRole(Role.EMPLOYEE))
                .totalAdmins(userRepository.countActiveByRole(Role.ADMIN))
                .totalRevenue(paymentRepository.sumTotalRevenue())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getClientStats() {
        User client = securityUtil.getCurrentUser();
        return DashboardStatsResponse.builder()
                .totalRequests(requestRepository.countByClientAndStatus(client, RequestStatus.PENDING)
                        + requestRepository.countByClientAndStatus(client, RequestStatus.COMPLETED)
                        + requestRepository.countByClientAndStatus(client, RequestStatus.IN_PROGRESS))
                .pendingRequests(requestRepository.countByClientAndStatus(client, RequestStatus.PENDING))
                .inProgressRequests(requestRepository.countByClientAndStatus(client, RequestStatus.IN_PROGRESS))
                .completedRequests(requestRepository.countByClientAndStatus(client, RequestStatus.COMPLETED))
                .totalSpent(paymentRepository.sumRevenueByClient(client))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getEmployeeStats() {
        User employee = securityUtil.getCurrentUser();
        long assigned = requestRepository.findByAssignedEmployeeAndStatus(employee, RequestStatus.IN_PROGRESS).size();
        long completed = requestRepository.findByAssignedEmployeeAndStatus(employee, RequestStatus.COMPLETED).size();
        return DashboardStatsResponse.builder()
                .assignedTasks(assigned)
                .completedTasks(completed)
                .inProgressRequests(assigned)
                .completedRequests(completed)
                .build();
    }
}
