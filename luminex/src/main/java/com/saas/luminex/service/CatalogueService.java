package com.saas.luminex.service;

import com.saas.luminex.entity.Category;
import com.saas.luminex.entity.Service;
import com.saas.luminex.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CatalogueService {
    List<Category> getActiveCategories();
    Page<Service> getActiveServices(Pageable pageable);
    Page<Service> searchServices(String query, Pageable pageable);
    Page<Service> getServicesByCategory(Long categoryId, Pageable pageable);
    List<Subscription> getActiveSubscriptions();
}
