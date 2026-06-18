package com.StartupSAAS.controller;

import com.StartupSAAS.dto.response.DocumentResponseDTO;
import com.StartupSAAS.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // POST /api/documents  multipart form: file + params
    @PostMapping
    public ResponseEntity<DocumentResponseDTO> upload(
            @RequestParam Long serviceRequestId,
            @RequestParam Long uploadedById,
            @RequestParam Long companyId,
            @RequestParam(required = false) String label,
            @RequestParam(required = false) String notes,
            @RequestPart("file") MultipartFile file) {
        return new ResponseEntity<>(
                documentService.upload(serviceRequestId, uploadedById,
                        companyId, label, notes, file),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public DocumentResponseDTO getById(@PathVariable Long id) {
        return documentService.getById(id);
    }

    @GetMapping("/service-request/{serviceRequestId}")
    public ResponseEntity<List<DocumentResponseDTO>> getByServiceRequest(
            @PathVariable Long serviceRequestId) {
        List<DocumentResponseDTO> list =
                documentService.getByServiceRequest(serviceRequestId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<DocumentResponseDTO>> getByCompany(
            @PathVariable Long companyId) {
        List<DocumentResponseDTO> list = documentService.getByCompany(companyId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        documentService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
