package com.saas.luminex.repository;

import com.saas.luminex.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByIsActiveTrue();
    Page<Service> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Service> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);
}
