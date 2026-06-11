package com.saas.luminex.service;

import com.saas.luminex.dto.request.KnowledgeBaseRequest;
import com.saas.luminex.entity.KnowledgeBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface KnowledgeBaseService {
    Page<KnowledgeBase> getAll(Pageable pageable);
    Page<KnowledgeBase> search(String query, Pageable pageable);
    Page<KnowledgeBase> getByCategory(String category, Pageable pageable);
    KnowledgeBase getById(Long id);
    KnowledgeBase create(KnowledgeBaseRequest request);
    KnowledgeBase update(Long id, KnowledgeBaseRequest request);
    void delete(Long id);
}
