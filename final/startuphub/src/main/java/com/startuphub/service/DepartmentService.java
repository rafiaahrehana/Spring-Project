package com.startuphub.service;

import com.startuphub.dto.request.DepartmentRequest;
import com.startuphub.dto.response.DepartmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {

    DepartmentResponse create(DepartmentRequest request);

    DepartmentResponse getById(Long id);

    Page<DepartmentResponse> listAll(Pageable pageable);

    List<DepartmentResponse> listActive();

    DepartmentResponse update(Long id, DepartmentRequest request);

    DepartmentResponse toggleActive(Long id);

    void delete(Long id);
}
