package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.HubServiceRequestDTO;
import com.StartupSAAS.dto.response.HubServiceResponseDTO;
import com.StartupSAAS.service.HubServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class HubServiceController {

    private final HubServiceService hubServiceService;

    // POST /api/services  multipart: "service" (JSON) + "icon" (file, optional)
    @PostMapping
    public ResponseEntity<HubServiceResponseDTO> create(
            @RequestPart("service") HubServiceRequestDTO dto,
            @RequestPart(value = "icon", required = false) MultipartFile icon) {
        return new ResponseEntity<>(hubServiceService.create(dto, icon), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HubServiceResponseDTO>> getAll() {
        List<HubServiceResponseDTO> list = hubServiceService.getAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public HubServiceResponseDTO getById(@PathVariable Long id) {
        return hubServiceService.getById(id);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<HubServiceResponseDTO>> getByCompany(
            @PathVariable Long companyId) {
        List<HubServiceResponseDTO> list = hubServiceService.getByCompany(companyId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/company/{companyId}/active")
    public ResponseEntity<List<HubServiceResponseDTO>> getActiveByCompany(
            @PathVariable Long companyId) {
        List<HubServiceResponseDTO> list = hubServiceService.getActiveByCompany(companyId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public HubServiceResponseDTO update(
            @PathVariable Long id,
            @RequestPart("service") HubServiceRequestDTO dto,
            @RequestPart(value = "icon", required = false) MultipartFile icon) {
        return hubServiceService.update(id, dto, icon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        hubServiceService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
