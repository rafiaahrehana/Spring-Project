package com.startuphub.repository;

import com.startuphub.entity.RequestStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestStatusHistoryRepository extends JpaRepository<RequestStatusHistory, Long> {

    List<RequestStatusHistory> findByServiceRequestIdOrderByChangedAtAsc(Long serviceRequestId);
}
