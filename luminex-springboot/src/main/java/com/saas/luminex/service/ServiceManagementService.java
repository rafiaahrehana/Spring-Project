package com.saas.luminex.service;

import com.saas.luminex.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceManagementService {
    Page<Service> getAll(Pageable pageable);
    Service getById(Long id);
    Service create(Service service, Long categoryId);
    Service update(Long id, Service service, Long categoryId);
    void delete(Long id);
    void toggleActive(Long id);
}
