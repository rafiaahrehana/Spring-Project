package com.saas.luminex.controller;

import com.saas.luminex.dto.response.ApiResponse;
import com.saas.luminex.entity.Category;
import com.saas.luminex.entity.Service;
import com.saas.luminex.entity.Subscription;
import com.saas.luminex.service.CategoryService;
import com.saas.luminex.service.ServiceManagementService;
import com.saas.luminex.service.SubscriptionService;
import com.saas.luminex.service.impl.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminCatalogueController {

    private final CategoryService categoryService;
    private final ServiceManagementService serviceManagementService;
    private final SubscriptionService subscriptionService;
    private final AuditLogService auditLogService;

    // ─── Categories ───────────────────────────────────────────────────────────

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAll()));
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        Category saved = categoryService.create(category);
        auditLogService.log("CREATE_CATEGORY", "Created category: " + saved.getName());
        return ResponseEntity.ok(ApiResponse.success("Category created", saved));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id, @RequestBody Category category) {
        Category updated = categoryService.update(id, category);
        auditLogService.log("UPDATE_CATEGORY", "Updated category ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Category updated", updated));
    }

    @PatchMapping("/categories/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleCategory(@PathVariable Long id) {
        categoryService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.success("Category status toggled", null));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        auditLogService.log("DELETE_CATEGORY", "Deleted category ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", null));
    }

    // ─── Services ─────────────────────────────────────────────────────────────

    @GetMapping("/services")
    public ResponseEntity<ApiResponse<Page<Service>>> getAllServices(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(serviceManagementService.getAll(pageable)));
    }

    @PostMapping("/services")
    public ResponseEntity<ApiResponse<Service>> createService(
            @RequestBody Map<String, Object> body) {
        Service service = buildServiceFromBody(body);
        Long categoryId = Long.valueOf(body.get("categoryId").toString());
        Service saved = serviceManagementService.create(service, categoryId);
        auditLogService.log("CREATE_SERVICE", "Created service: " + saved.getName());
        return ResponseEntity.ok(ApiResponse.success("Service created", saved));
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<ApiResponse<Service>> updateService(
            @PathVariable Long id, @RequestBody Map<String, Object> body) {
        Service service = buildServiceFromBody(body);
        Long categoryId = Long.valueOf(body.get("categoryId").toString());
        Service updated = serviceManagementService.update(id, service, categoryId);
        auditLogService.log("UPDATE_SERVICE", "Updated service ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Service updated", updated));
    }

    @PatchMapping("/services/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleService(@PathVariable Long id) {
        serviceManagementService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.success("Service status toggled", null));
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id) {
        serviceManagementService.delete(id);
        auditLogService.log("DELETE_SERVICE", "Deleted service ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Service deleted", null));
    }

    // ─── Subscriptions ────────────────────────────────────────────────────────

    @GetMapping("/subscriptions")
    public ResponseEntity<ApiResponse<List<Subscription>>> getAllSubscriptions() {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getAll()));
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<ApiResponse<Subscription>> createSubscription(
            @RequestBody Subscription subscription) {
        Subscription saved = subscriptionService.create(subscription);
        auditLogService.log("CREATE_SUBSCRIPTION", "Created plan: " + saved.getName());
        return ResponseEntity.ok(ApiResponse.success("Subscription created", saved));
    }

    @PutMapping("/subscriptions/{id}")
    public ResponseEntity<ApiResponse<Subscription>> updateSubscription(
            @PathVariable Long id, @RequestBody Subscription subscription) {
        Subscription updated = subscriptionService.update(id, subscription);
        auditLogService.log("UPDATE_SUBSCRIPTION", "Updated plan ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Subscription updated", updated));
    }

    @DeleteMapping("/subscriptions/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubscription(@PathVariable Long id) {
        subscriptionService.delete(id);
        auditLogService.log("DELETE_SUBSCRIPTION", "Deleted plan ID: " + id);
        return ResponseEntity.ok(ApiResponse.success("Subscription deleted", null));
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Service buildServiceFromBody(Map<String, Object> body) {
        Service service = new Service();
        service.setName((String) body.get("name"));
        service.setDescription((String) body.get("description"));
        service.setPrice(new java.math.BigDecimal(body.get("price").toString()));
        service.setPriceType(
                com.saas.luminex.enums.PriceType.valueOf(body.get("priceType").toString()));
        if (body.get("deliveryDays") != null) {
            service.setDeliveryDays(Integer.valueOf(body.get("deliveryDays").toString()));
        }
        return service;
    }
}
