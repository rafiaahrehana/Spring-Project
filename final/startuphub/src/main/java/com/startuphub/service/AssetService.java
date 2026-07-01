package com.startuphub.service;

import com.startuphub.dto.request.AssetRequest;
import com.startuphub.dto.response.AssetResponse;
import com.startuphub.enums.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssetService {
    AssetResponse create(AssetRequest request);
    AssetResponse getById(Long id);
    Page<AssetResponse> listAll(AssetStatus status, Pageable pageable);
    List<AssetResponse> listForEmployee(Long employeeId);
    AssetResponse update(Long id, AssetRequest request);
    AssetResponse assign(Long id, Long employeeId);
    AssetResponse unassign(Long id);
    void delete(Long id);
}
