package com.startuphub.service;

import com.startuphub.dto.request.HubServiceRequest;
import com.startuphub.dto.response.HubServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HubServiceService {

    HubServiceResponse create(HubServiceRequest request);

    HubServiceResponse getById(Long id);

    Page<HubServiceResponse> listAll(Long categoryId, Pageable pageable);

    List<HubServiceResponse> listActive();

    HubServiceResponse update(Long id, HubServiceRequest request);

    HubServiceResponse toggleActive(Long id);

    void delete(Long id);
}
