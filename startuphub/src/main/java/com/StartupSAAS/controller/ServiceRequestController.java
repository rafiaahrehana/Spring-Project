package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.ServiceRequestDTO;
import com.StartupSAAS.dto.request.ServiceRequestStatusDTO;
import com.StartupSAAS.dto.response.ServiceRequestResponseDTO;
import com.StartupSAAS.enums.RequestStatus;
import com.StartupSAAS.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
@RequiredArgsConstructor
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    @PostMapping
    public ResponseEntity<ServiceRequestResponseDTO> create(
            @RequestBody ServiceRequestDTO dto) {
        return new ResponseEntity<>(serviceRequestService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ServiceRequestResponseDTO>> getAll() {
        List<ServiceRequestResponseDTO> list = serviceRequestService.getAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ServiceRequestResponseDTO getById(@PathVariable Long id) {
        return serviceRequestService.getById(id);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ServiceRequestResponseDTO>> getByCompany(
            @PathVariable Long companyId) {
        List<ServiceRequestResponseDTO> list = serviceRequestService.getByCompany(companyId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ServiceRequestResponseDTO>> getByClient(
            @PathVariable Long clientId) {
        List<ServiceRequestResponseDTO> list = serviceRequestService.getByClient(clientId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ServiceRequestResponseDTO>> getByEmployee(
            @PathVariable Long employeeId) {
        List<ServiceRequestResponseDTO> list = serviceRequestService.getByEmployee(employeeId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    // GET /api/service-requests/company/3/status/IN_PROGRESS
    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<List<ServiceRequestResponseDTO>> getByCompanyAndStatus(
            @PathVariable Long companyId, @PathVariable String status) {
        RequestStatus rs;
        try { rs = RequestStatus.valueOf(status.toUpperCase()); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().build(); }
        List<ServiceRequestResponseDTO> list =
                serviceRequestService.getByCompanyAndStatus(companyId, rs);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    // PATCH /api/service-requests/1/status
    @PatchMapping("/{id}/status")
    public ServiceRequestResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestBody ServiceRequestStatusDTO dto) {
        return serviceRequestService.updateStatus(id, dto);
    }

    @PutMapping("/{id}")
    public ServiceRequestResponseDTO update(
            @PathVariable Long id,
            @RequestBody ServiceRequestDTO dto) {
        return serviceRequestService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        serviceRequestService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
