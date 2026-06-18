package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.ClientRequestDTO;
import com.StartupSAAS.dto.response.ClientResponseDTO;
import com.StartupSAAS.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    // POST /api/clients
    // multipart: "client" (JSON part) + "image" (file, optional)
    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(
            @RequestPart("client") ClientRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return new ResponseEntity<>(clientService.create(dto, image), HttpStatus.CREATED);
    }

    // GET /api/clients
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAll() {
        List<ClientResponseDTO> list = clientService.getAll();
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    // GET /api/clients/1
    @GetMapping("/{id}")
    public ClientResponseDTO getById(@PathVariable Long id) {
        return clientService.getById(id);
    }

    // GET /api/clients/company/3
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ClientResponseDTO>> getByCompany(
            @PathVariable Long companyId) {
        List<ClientResponseDTO> list = clientService.getByCompany(companyId);
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    // PUT /api/clients/1
    @PutMapping("/{id}")
    public ClientResponseDTO update(
            @PathVariable Long id,
            @RequestPart("client") ClientRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return clientService.update(id, dto, image);
    }

    // DELETE /api/clients/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.ok("Client deleted successfully");
    }
}
