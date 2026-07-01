package com.startuphub.service;

import com.startuphub.dto.request.PerformanceReviewRequest;
import com.startuphub.dto.response.PerformanceReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PerformanceService {

    PerformanceReviewResponse create(PerformanceReviewRequest request);

    PerformanceReviewResponse getById(Long id);

    Page<PerformanceReviewResponse> listAll(Pageable pageable);

    Page<PerformanceReviewResponse> listForEmployee(Long employeeId, Pageable pageable);

    PerformanceReviewResponse update(Long id, PerformanceReviewRequest request);

    PerformanceReviewResponse finalise(Long id);

    void delete(Long id);
}
