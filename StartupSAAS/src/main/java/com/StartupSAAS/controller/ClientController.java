package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.ClientRequest;
import com.StartupSAAS.dto.response.ClientResponse;
import com.StartupSAAS.service.ClientService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
  private final ClientService clientService;

  @PostMapping(
      value = "/{companyId}",
      consumes = {"multipart/form-data"})
  public ResponseEntity<ClientResponse> create(
      @PathVariable Long companyId,
      @RequestPart("data") ClientRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    return new ResponseEntity<>(
        clientService.createClient(companyId, request, image), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ClientResponse getById(@PathVariable Long id) {
    return clientService.getClientById(id);
  }

  @GetMapping
  public ResponseEntity<List<ClientResponse>> getAll() {
    return ResponseEntity.ok(clientService.getAllClients());
  }

  @GetMapping("/company/{companyId}")
  public ResponseEntity<List<ClientResponse>> getByCompanyId(@PathVariable Long companyId) {
    return ResponseEntity.ok(clientService.getClientsByCompanyId(companyId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Long id) {
    clientService.deleteClient(id);
    return ResponseEntity.ok("Deleted successfully");
  }
}
