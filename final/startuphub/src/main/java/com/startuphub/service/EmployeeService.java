package com.startuphub.service;

import com.startuphub.dto.request.CreateEmployeeRequest;
import com.startuphub.dto.request.UpdateEmployeeRequest;
import com.startuphub.dto.response.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeResponse create(CreateEmployeeRequest request);

    EmployeeResponse getById(Long id);

    EmployeeResponse getMyProfile();

    Page<EmployeeResponse> listAll(Long departmentId, Pageable pageable);

    EmployeeResponse update(Long id, UpdateEmployeeRequest request);

    void terminate(Long id);
}
