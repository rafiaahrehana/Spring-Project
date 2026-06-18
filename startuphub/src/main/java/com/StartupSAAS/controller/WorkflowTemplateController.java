package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.WorkflowTemplateRequestDTO;
import com.StartupSAAS.dto.response.WorkflowTemplateResponseDTO;
import com.StartupSAAS.service.WorkflowTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow-templates")
@RequiredArgsConstructor
public class WorkflowTemplateController {

    private final WorkflowTemplateService workflowTemplateService;

    @PostMapping
    public ResponseEntity<WorkflowTemplateResponseDTO> create(
            @RequestBody WorkflowTemplateRequestDTO dto) {
        return new ResponseEntity<>(workflowTemplateService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<WorkflowTemplateResponseDTO>> getAll() {
        List<WorkflowTemplateResponseDTO> list = workflowTemplateService.getAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public WorkflowTemplateResponseDTO getById(@PathVariable Long id) {
        return workflowTemplateService.getById(id);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<WorkflowTemplateResponseDTO>> getByCompany(
            @PathVariable Long companyId) {
        List<WorkflowTemplateResponseDTO> list = workflowTemplateService.getByCompany(companyId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public WorkflowTemplateResponseDTO update(
            @PathVariable Long id,
            @RequestBody WorkflowTemplateRequestDTO dto) {
        return workflowTemplateService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        workflowTemplateService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
