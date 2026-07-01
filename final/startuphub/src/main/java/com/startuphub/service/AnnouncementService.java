package com.startuphub.service;

import com.startuphub.dto.request.AnnouncementRequest;
import com.startuphub.dto.response.AnnouncementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnnouncementService {

    AnnouncementResponse create(AnnouncementRequest request);

    AnnouncementResponse getById(Long id);

    Page<AnnouncementResponse> listAll(Pageable pageable);

    List<AnnouncementResponse> listActive();

    AnnouncementResponse publish(Long id);

    AnnouncementResponse update(Long id, AnnouncementRequest request);

    void delete(Long id);
}
