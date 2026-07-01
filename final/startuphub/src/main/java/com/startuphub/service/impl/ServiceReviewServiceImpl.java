package com.startuphub.service.impl;

import com.startuphub.dto.request.ServiceReviewRequest;
import com.startuphub.dto.response.ServiceReviewResponse;
import com.startuphub.entity.Client;
import com.startuphub.entity.Company;
import com.startuphub.entity.HubService;
import com.startuphub.entity.ServiceRequest;
import com.startuphub.entity.ServiceReview;
import com.startuphub.enums.ServiceRequestStatus;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.Phase8Mapper;
import com.startuphub.repository.ClientRepository;
import com.startuphub.repository.HubServiceRepository;
import com.startuphub.repository.ServiceRequestRepository;
import com.startuphub.repository.ServiceReviewRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.ServiceReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceReviewServiceImpl implements ServiceReviewService {

    private final ServiceReviewRepository  reviewRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final HubServiceRepository     hubServiceRepository;
    private final ClientRepository         clientRepository;
    private final SecurityUtil             securityUtil;

    @Override
    @Transactional
    public ServiceReviewResponse submit(ServiceReviewRequest request) {
        Long companyId = requireCompanyId();

        ServiceRequest sr = serviceRequestRepository
            .findByIdAndCompanyId(request.serviceRequestId(), companyId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Service request not found: " + request.serviceRequestId()));

        if (sr.getStatus() != ServiceRequestStatus.COMPLETED) {
            throw new BadRequestException("Reviews can only be submitted for completed requests");
        }

        Client client = clientRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Only clients can submit reviews"));

        if (!sr.getClient().getId().equals(client.getId())) {
            throw new BadRequestException("You can only review your own service requests");
        }
        if (reviewRepository.existsByServiceRequestIdAndClientId(sr.getId(), client.getId())) {
            throw new BadRequestException("You have already reviewed this service request");
        }

        ServiceReview review = ServiceReview.builder()
            .serviceRequest(sr)
            .hubService(sr.getHubService())
            .client(client)
            .company(companyRef(companyId))
            .rating(request.rating())
            .comment(request.comment())
            .published(true)
            .build();

        reviewRepository.save(review);
        log.info("Review submitted: serviceRequestId={} rating={}", sr.getId(), request.rating());
        return Phase8Mapper.toServiceReviewResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceReviewResponse getById(Long id) {
        return Phase8Mapper.toServiceReviewResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceReviewResponse> listAll(Pageable pageable) {
        return reviewRepository.findByCompanyId(requireCompanyId(), pageable)
            .map(Phase8Mapper::toServiceReviewResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceReviewResponse> listByService(Long hubServiceId, Pageable pageable) {
        return reviewRepository.findByCompanyIdAndHubServiceId(requireCompanyId(), hubServiceId, pageable)
            .map(Phase8Mapper::toServiceReviewResponse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findInTenant(id).softDelete();
    }

    private ServiceReview findInTenant(Long id) {
        return reviewRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Service review not found: " + id));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }

    private Company companyRef(Long companyId) {
        Company c = new Company(); c.setId(companyId); return c;
    }
}
