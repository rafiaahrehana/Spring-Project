package com.startuphub.service.impl;

import com.startuphub.dto.request.PerformanceReviewRequest;
import com.startuphub.dto.response.PerformanceReviewResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.entity.PerformanceReview;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.Phase8Mapper;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.PerformanceReviewRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceServiceImpl implements PerformanceService {

    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeRepository          employeeRepository;
    private final SecurityUtil                securityUtil;

    @Override
    @Transactional
    public PerformanceReviewResponse create(PerformanceReviewRequest request) {
        Long companyId = requireCompanyId();
        Employee employee = employeeRepository.findByIdAndCompanyId(request.employeeId(), companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + request.employeeId()));
        Employee reviewer = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));

        PerformanceReview review = PerformanceReview.builder()
            .employee(employee)
            .company(companyRef(companyId))
            .reviewedBy(reviewer)
            .reviewPeriodStart(request.reviewPeriodStart())
            .reviewPeriodEnd(request.reviewPeriodEnd())
            .scoreWorkQuality(request.scoreWorkQuality())
            .scoreProductivity(request.scoreProductivity())
            .scoreCommunication(request.scoreCommunication())
            .scoreTeamwork(request.scoreTeamwork())
            .scoreInitiative(request.scoreInitiative())
            .scorePunctuality(request.scorePunctuality())
            .overallScore(calculateOverall(request))
            .strengths(request.strengths())
            .areasForImprovement(request.areasForImprovement())
            .goalsForNextPeriod(request.goalsForNextPeriod())
            .comments(request.comments())
            .build();

        reviewRepository.save(review);
        return Phase8Mapper.toPerformanceReviewResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceReviewResponse getById(Long id) {
        return Phase8Mapper.toPerformanceReviewResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PerformanceReviewResponse> listAll(Pageable pageable) {
        return reviewRepository.findByCompanyId(requireCompanyId(), pageable)
            .map(Phase8Mapper::toPerformanceReviewResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PerformanceReviewResponse> listForEmployee(Long employeeId, Pageable pageable) {
        return reviewRepository.findByCompanyIdAndEmployeeId(requireCompanyId(), employeeId, pageable)
            .map(Phase8Mapper::toPerformanceReviewResponse);
    }

    @Override
    @Transactional
    public PerformanceReviewResponse update(Long id, PerformanceReviewRequest request) {
        PerformanceReview review = findInTenant(id);
        if (review.isFinalised()) throw new BadRequestException("Cannot edit a finalised review");
        if (request.scoreWorkQuality()    != null) review.setScoreWorkQuality(request.scoreWorkQuality());
        if (request.scoreProductivity()   != null) review.setScoreProductivity(request.scoreProductivity());
        if (request.scoreCommunication()  != null) review.setScoreCommunication(request.scoreCommunication());
        if (request.scoreTeamwork()       != null) review.setScoreTeamwork(request.scoreTeamwork());
        if (request.scoreInitiative()     != null) review.setScoreInitiative(request.scoreInitiative());
        if (request.scorePunctuality()    != null) review.setScorePunctuality(request.scorePunctuality());
        if (request.strengths()           != null) review.setStrengths(request.strengths());
        if (request.areasForImprovement() != null) review.setAreasForImprovement(request.areasForImprovement());
        if (request.goalsForNextPeriod()  != null) review.setGoalsForNextPeriod(request.goalsForNextPeriod());
        if (request.comments()            != null) review.setComments(request.comments());
        review.setOverallScore(calculateOverallFromReview(review));
        return Phase8Mapper.toPerformanceReviewResponse(review);
    }

    @Override
    @Transactional
    public PerformanceReviewResponse finalise(Long id) {
        PerformanceReview review = findInTenant(id);
        if (review.isFinalised()) throw new BadRequestException("Review is already finalised");
        review.setFinalised(true);
        return Phase8Mapper.toPerformanceReviewResponse(review);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PerformanceReview review = findInTenant(id);
        if (review.isFinalised()) throw new BadRequestException("Cannot delete a finalised review");
        review.softDelete();
    }

    private PerformanceReview findInTenant(Long id) {
        return reviewRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Performance review not found: " + id));
    }

    private Double calculateOverall(PerformanceReviewRequest r) {
        long count = Stream.of(r.scoreWorkQuality(), r.scoreProductivity(), r.scoreCommunication(),
            r.scoreTeamwork(), r.scoreInitiative(), r.scorePunctuality()).filter(s -> s != null).count();
        if (count == 0) return null;
        int sum = Stream.of(r.scoreWorkQuality(), r.scoreProductivity(), r.scoreCommunication(),
            r.scoreTeamwork(), r.scoreInitiative(), r.scorePunctuality())
            .filter(s -> s != null).mapToInt(Integer::intValue).sum();
        return Math.round((double) sum / count * 10.0) / 10.0;
    }

    private Double calculateOverallFromReview(PerformanceReview r) {
        long count = Stream.of(r.getScoreWorkQuality(), r.getScoreProductivity(), r.getScoreCommunication(),
            r.getScoreTeamwork(), r.getScoreInitiative(), r.getScorePunctuality()).filter(s -> s != null).count();
        if (count == 0) return null;
        int sum = Stream.of(r.getScoreWorkQuality(), r.getScoreProductivity(), r.getScoreCommunication(),
            r.getScoreTeamwork(), r.getScoreInitiative(), r.getScorePunctuality())
            .filter(s -> s != null).mapToInt(Integer::intValue).sum();
        return Math.round((double) sum / count * 10.0) / 10.0;
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
