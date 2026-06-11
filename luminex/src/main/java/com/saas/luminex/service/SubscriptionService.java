package com.saas.luminex.service;

import com.saas.luminex.entity.Subscription;

import java.util.List;

public interface SubscriptionService {
    List<Subscription> getAll();
    List<Subscription> getAllActive();
    Subscription getById(Long id);
    Subscription create(Subscription subscription);
    Subscription update(Long id, Subscription subscription);
    void delete(Long id);
}
