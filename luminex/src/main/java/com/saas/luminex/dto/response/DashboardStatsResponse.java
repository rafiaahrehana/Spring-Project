package com.saas.luminex.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardStatsResponse {
    // Common
    private long totalRequests;
    private long pendingRequests;
    private long inProgressRequests;
    private long completedRequests;

    // Admin / SuperAdmin
    private long totalClients;
    private long totalEmployees;
    private long totalAdmins;
    private BigDecimal totalRevenue;
    private long unpaidPayments;

    // Client specific
    private BigDecimal totalSpent;
    private long activeRequests;

    // Employee specific
    private long assignedTasks;
    private long completedTasks;
}
