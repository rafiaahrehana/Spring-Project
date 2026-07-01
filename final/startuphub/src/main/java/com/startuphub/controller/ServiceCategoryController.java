package com.startuphub.controller;

import com.startuphub.dto.request.ServiceCategoryRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.ServiceCategoryResponse;
import com.startuphub.entity.ServiceCategory;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.ServiceCategoryMapper;
import com.startuphub.repository.ServiceCategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Service category management — platform-wide taxonomy.
 *
 * GET endpoints are public (no auth) — Angular catalog page shows
 * categories before users log in.
 *
 * POST/PUT/PATCH/DELETE require SUPER_ADMIN role.
 */
@RestController
@RequestMapping("/api/service-categories")
@RequiredArgsConstructor
@Tag(name = "Service Categories", description = "Platform-wide service taxonomy (Legal, Finance, HR, IT, Office Space, Stationery)")
public class ServiceCategoryController {

    private final ServiceCategoryRepository categoryRepository;

    @GetMapping
    @Operation(summary = "Get all active service categories — public endpoint")
    public ResponseEntity<ApiResponse<List<ServiceCategoryResponse>>> getAll() {
        List<ServiceCategoryResponse> categories = categoryRepository
            .findByActiveTrueOrderBySortOrderAsc()
            .stream()
            .map(ServiceCategoryMapper::toResponse)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a service category by ID")
    public ResponseEntity<ApiResponse<ServiceCategoryResponse>> getById(@PathVariable Long id) {
        ServiceCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Service category not found: " + id));
        return ResponseEntity.ok(ApiResponse.success(ServiceCategoryMapper.toResponse(category)));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    @Operation(summary = "Create a new service category — SUPER_ADMIN only")
    public ResponseEntity<ApiResponse<ServiceCategoryResponse>> create(
            @Valid @RequestBody ServiceCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new BadRequestException("A service category with this name already exists");
        }
        ServiceCategory category = ServiceCategory.builder()
            .name(request.name())
            .nameBn(request.nameBn())
            .description(request.description())
            .iconUrl(request.iconUrl())
            .sortOrder(request.sortOrder())
            .active(true)
            .build();
        categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Service category created", ServiceCategoryMapper.toResponse(category)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    @Operation(summary = "Update a service category — SUPER_ADMIN only")
    public ResponseEntity<ApiResponse<ServiceCategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceCategoryRequest request) {
        ServiceCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Service category not found: " + id));
        category.setName(request.name());
        category.setNameBn(request.nameBn());
        category.setDescription(request.description());
        category.setIconUrl(request.iconUrl());
        category.setSortOrder(request.sortOrder());
        return ResponseEntity.ok(
            ApiResponse.success("Service category updated", ServiceCategoryMapper.toResponse(category)));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    @Operation(summary = "Toggle active/inactive status — SUPER_ADMIN only")
    public ResponseEntity<ApiResponse<ServiceCategoryResponse>> toggle(@PathVariable Long id) {
        ServiceCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Service category not found: " + id));
        category.setActive(!category.isActive());
        return ResponseEntity.ok(ApiResponse.success(
            "Category " + (category.isActive() ? "activated" : "deactivated"),
            ServiceCategoryMapper.toResponse(category)));
    }
}
