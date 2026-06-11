package com.saas.luminex.controller;

import com.saas.luminex.dto.response.ApiResponse;
import com.saas.luminex.entity.Category;
import com.saas.luminex.entity.Service;
import com.saas.luminex.entity.Subscription;
import com.saas.luminex.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogueController {

    private final CatalogueService catalogueService;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(catalogueService.getActiveCategories()));
    }

    @GetMapping("/services")
    public ResponseEntity<ApiResponse<Page<Service>>> getServices(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(catalogueService.searchServices(search, pageable)));
        }
        return ResponseEntity.ok(ApiResponse.success(catalogueService.getActiveServices(pageable)));
    }

    @GetMapping("/services/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<Service>>> getServicesByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                catalogueService.getServicesByCategory(categoryId, pageable)));
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<ApiResponse<List<Subscription>>> getSubscriptions() {
        return ResponseEntity.ok(ApiResponse.success(catalogueService.getActiveSubscriptions()));
    }
}
