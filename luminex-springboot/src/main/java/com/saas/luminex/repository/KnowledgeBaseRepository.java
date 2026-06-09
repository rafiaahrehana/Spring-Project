package com.saas.luminex.repository;

import com.saas.luminex.entity.KnowledgeBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {
    Page<KnowledgeBase> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<KnowledgeBase> findByCategoryIgnoreCase(String category, Pageable pageable);
}
