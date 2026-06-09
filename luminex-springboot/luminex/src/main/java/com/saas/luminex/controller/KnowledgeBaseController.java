package com.saas.luminex.controller;

import com.saas.luminex.dto.request.KnowledgeBaseRequest;
import com.saas.luminex.dto.response.ApiResponse;
import com.saas.luminex.entity.KnowledgeBase;
import com.saas.luminex.service.impl.AuditLogService;
import com.saas.luminex.service.impl.KnowledgeBaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledge-base")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;
    private final AuditLogService auditLogService;

    // ─── Read — available to EMPLOYEE, ADMIN, SUPER_ADMIN ────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<KnowledgeBase>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<KnowledgeBase> result;
        if (search != null && !search.isBlank()) {
            result = knowledgeBaseService.search(search, pageable);
        } else if (category != null && !category.isBlank()) {
            result = knowledgeBaseService.getByCategory(category, pageable);
        } else {
            result = knowledgeBaseService.getAll(pageable);
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<KnowledgeBase>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(knowledgeBaseService.getById(id)));
    }

    // ─── Write — ADMIN + SUPER_ADMIN only ────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<KnowledgeBase>> create(
            @Valid @RequestBody KnowledgeBaseRequest request) {
        KnowledgeBase saved = knowledgeBaseService.create(request);
        auditLogService.log("CREATE_KB_ARTICLE", "Created article: " + saved.getTitle());
        return ResponseEntity.ok(ApiResponse.success("Article created", saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<KnowledgeBase>> update(
            @PathVariable Long id,
            @Valid @RequestBody KnowledgeBaseRequest request) {
        KnowledgeBase updated = knowledgeBaseService.update(id, request);
        auditLogService.log("UPDATE_KB_ARTICLE", "Updated article ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Article updated", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        knowledgeBaseService.delete(id);
        auditLogService.log("DELETE_KB_ARTICLE", "Deleted article ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Article deleted", null));
    }
}
