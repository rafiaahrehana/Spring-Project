package com.startuphub.service;

import com.startuphub.dto.request.EmploymentLetterRequest;
import com.startuphub.dto.response.EmploymentLetterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmploymentLetterService {

    EmploymentLetterResponse create(EmploymentLetterRequest request);

    EmploymentLetterResponse getById(Long id);

    Page<EmploymentLetterResponse> listAll(Pageable pageable);

    Page<EmploymentLetterResponse> listForEmployee(Long employeeId, Pageable pageable);

    EmploymentLetterResponse issue(Long id);

    void delete(Long id);
}
