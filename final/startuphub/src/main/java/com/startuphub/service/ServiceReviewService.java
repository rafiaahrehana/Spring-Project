package com.startuphub.service;

import com.startuphub.dto.request.ServiceReviewRequest;
import com.startuphub.dto.response.ServiceReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceReviewService {

    ServiceReviewResponse submit(ServiceReviewRequest request);

    ServiceReviewResponse getById(Long id);

    Page<ServiceReviewResponse> listAll(Pageable pageable);

    Page<ServiceReviewResponse> listByService(Long hubServiceId, Pageable pageable);

    void delete(Long id);
}
