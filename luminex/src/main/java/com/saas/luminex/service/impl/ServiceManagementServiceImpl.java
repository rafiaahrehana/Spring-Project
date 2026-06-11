package com.saas.luminex.service.impl;

import com.saas.luminex.entity.Category;
import com.saas.luminex.entity.Service;
import com.saas.luminex.exception.ResourceNotFoundException;
import com.saas.luminex.repository.CategoryRepository;
import com.saas.luminex.repository.ServiceRepository;
import com.saas.luminex.service.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Service> getAll(Pageable pageable) {
        return serviceRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Service getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
    }

    @Override
    @Transactional
    public Service create(Service service, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
        service.setCategory(category);
        return serviceRepository.save(service);
    }

    @Override
    @Transactional
    public Service update(Long id, Service incoming, Long categoryId) {
        Service existing = getById(id);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setPrice(incoming.getPrice());
        existing.setPriceType(incoming.getPriceType());
        existing.setDeliveryDays(incoming.getDeliveryDays());
        existing.setCategory(category);
        return serviceRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service", id);
        }
        serviceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleActive(Long id) {
        Service service = getById(id);
        service.setActive(!service.isActive());
        serviceRepository.save(service);
    }
}
