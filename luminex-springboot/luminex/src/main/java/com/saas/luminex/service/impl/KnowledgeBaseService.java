package com.saas.luminex.service.impl;

import com.saas.luminex.dto.request.KnowledgeBaseRequest;
import com.saas.luminex.entity.KnowledgeBase;
import com.saas.luminex.exception.ResourceNotFoundException;
import com.saas.luminex.repository.KnowledgeBaseRepository;
import com.saas.luminex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final SecurityUtil securityUtil;

    @Transactional(readOnly = true)
    public Page<KnowledgeBase> getAll(Pageable pageable) {
        return knowledgeBaseRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<KnowledgeBase> search(String query, Pageable pageable) {
        return knowledgeBaseRepository.findByTitleContainingIgnoreCase(query, pageable);
    }

    @Transactional(readOnly = true)
    public Page<KnowledgeBase> getByCategory(String category, Pageable pageable) {
        return knowledgeBaseRepository.findByCategoryIgnoreCase(category, pageable);
    }

    @Transactional(readOnly = true)
    public KnowledgeBase getById(Long id) {
        return knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KnowledgeBase", id));
    }

    @Transactional
    public KnowledgeBase create(KnowledgeBaseRequest request) {
        KnowledgeBase article = KnowledgeBase.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .content(request.getContent())
                .createdBy(securityUtil.getCurrentUser())
                .build();
        return knowledgeBaseRepository.save(article);
    }

    @Transactional
    public KnowledgeBase update(Long id, KnowledgeBaseRequest request) {
        KnowledgeBase existing = getById(id);
        existing.setTitle(request.getTitle());
        existing.setCategory(request.getCategory());
        existing.setContent(request.getContent());
        return knowledgeBaseRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!knowledgeBaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("KnowledgeBase", id);
        }
        knowledgeBaseRepository.deleteById(id);
    }
}
