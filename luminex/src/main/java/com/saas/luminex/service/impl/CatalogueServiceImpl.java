package com.saas.luminex.service.impl;

import com.saas.luminex.entity.Category;
import com.saas.luminex.entity.Service;
import com.saas.luminex.entity.Subscription;
import com.saas.luminex.repository.CategoryRepository;
import com.saas.luminex.repository.ServiceRepository;
import com.saas.luminex.repository.SubscriptionRepository;
import com.saas.luminex.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CatalogueServiceImpl implements CatalogueService {

    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Service> getActiveServices(Pageable pageable) {
        return serviceRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Service> searchServices(String query, Pageable pageable) {
        return serviceRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(query, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Service> getServicesByCategory(Long categoryId, Pageable pageable) {
        return serviceRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> getActiveSubscriptions() {
        return subscriptionRepository.findByIsActiveTrue();
    }
}
