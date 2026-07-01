package com.startuphub.service.impl;

import com.startuphub.dto.request.AnnouncementRequest;
import com.startuphub.dto.request.CreateNotificationRequest;
import com.startuphub.dto.response.AnnouncementResponse;
import com.startuphub.entity.Announcement;
import com.startuphub.entity.Company;
import com.startuphub.enums.NotificationType;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.Phase8Mapper;
import com.startuphub.repository.AnnouncementRepository;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.AnnouncementService;
import com.startuphub.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final EmployeeRepository     employeeRepository;
    private final NotificationService    notificationService;
    private final SecurityUtil           securityUtil;

    @Override
    @Transactional
    public AnnouncementResponse create(AnnouncementRequest request) {
        Long companyId = requireCompanyId();
        Announcement announcement = Announcement.builder()
            .title(request.title())
            .body(request.body())
            .expiresAt(request.expiresAt())
            .notifyAll(request.notifyAll())
            .published(false)
            .company(companyRef(companyId))
            .createdBy(securityUtil.getCurrentUser())
            .build();
        announcementRepository.save(announcement);
        return Phase8Mapper.toAnnouncementResponse(announcement);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponse getById(Long id) {
        return Phase8Mapper.toAnnouncementResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementResponse> listAll(Pageable pageable) {
        return announcementRepository.findByCompanyId(requireCompanyId(), pageable)
            .map(Phase8Mapper::toAnnouncementResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> listActive() {
        return announcementRepository.findActiveByCompanyId(requireCompanyId(), LocalDateTime.now())
            .stream().map(Phase8Mapper::toAnnouncementResponse).toList();
    }

    @Override
    @Transactional
    public AnnouncementResponse publish(Long id) {
        Long companyId = requireCompanyId();
        Announcement announcement = findInTenant(id);
        if (announcement.isPublished()) throw new BadRequestException("Announcement is already published");
        announcement.setPublished(true);
        announcement.setPublishedAt(LocalDateTime.now());

        if (announcement.isNotifyAll()) {
            // Process in pages of 100 to avoid loading all employees into memory
            int pageNum = 0;
            final int PAGE_SIZE = 100;
            org.springframework.data.domain.Page<com.startuphub.entity.Employee> page;
            do {
                page = employeeRepository.findByCompanyId(companyId,
                    org.springframework.data.domain.PageRequest.of(pageNum, PAGE_SIZE));
                page.getContent().forEach(emp -> {
                    if (emp.getUser() != null) {
                        notificationService.send(CreateNotificationRequest.of(
                            NotificationType.ANNOUNCEMENT,
                            announcement.getTitle(),
                            announcement.getBody().length() > 150
                                ? announcement.getBody().substring(0, 147) + "..."
                                : announcement.getBody(),
                            "/announcements/" + announcement.getId(),
                            emp.getUser().getId(),
                            companyId
                        ));
                    }
                });
                pageNum++;
            } while (page.hasNext());
        }
        log.info("Announcement published: id={} company={}", id, companyId);
        return Phase8Mapper.toAnnouncementResponse(announcement);
    }

    @Override
    @Transactional
    public AnnouncementResponse update(Long id, AnnouncementRequest request) {
        Announcement announcement = findInTenant(id);
        if (announcement.isPublished()) throw new BadRequestException("Cannot edit a published announcement");
        if (request.title() != null) announcement.setTitle(request.title());
        if (request.body()  != null) announcement.setBody(request.body());
        announcement.setExpiresAt(request.expiresAt());
        announcement.setNotifyAll(request.notifyAll());
        return Phase8Mapper.toAnnouncementResponse(announcement);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Announcement announcement = findInTenant(id);
        if (announcement.isPublished()) throw new BadRequestException("Cannot delete a published announcement");
        announcement.softDelete();
    }

    private Announcement findInTenant(Long id) {
        return announcementRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Announcement not found: " + id));
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
