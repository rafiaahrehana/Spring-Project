package com.saas.luminex.service.impl;

import com.saas.luminex.entity.Subscription;
import com.saas.luminex.exception.ResourceNotFoundException;
import com.saas.luminex.repository.SubscriptionRepository;
import com.saas.luminex.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> getAll() {
        return subscriptionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> getAllActive() {
        return subscriptionRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Subscription getById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", id));
    }

    @Override
    @Transactional
    public Subscription create(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public Subscription update(Long id, Subscription incoming) {
        Subscription existing = getById(id);
        existing.setName(incoming.getName());
        existing.setPrice(incoming.getPrice());
        existing.setFeaturesJson(incoming.getFeaturesJson());
        existing.setRecommended(incoming.isRecommended());
        existing.setActive(incoming.isActive());
        return subscriptionRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscription", id);
        }
        subscriptionRepository.deleteById(id);
    }
}
