package com.saas.luminex.service;

import com.saas.luminex.dto.response.DashboardStatsResponse;

public interface DashboardService {
    DashboardStatsResponse getAdminStats();
    DashboardStatsResponse getClientStats();
    DashboardStatsResponse getEmployeeStats();
}
