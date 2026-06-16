package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.ClientMapper;
import com.StartupSAAS.dto.request.ClientRequest;
import com.StartupSAAS.dto.response.ClientResponse;
import com.StartupSAAS.entity.Client;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.repository.ClientRepository;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.ClientService;
import jakarta.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;
  private final PasswordEncoder passwordEncoder;

  @Value("${image.upload.dir}")
  private String uploadDir;

  @Override
  @Transactional
  public ClientResponse createClient(Long companyId, ClientRequest request, MultipartFile image) {
    // 1. Validate company
    Company company =
        companyRepository
            .findById(companyId)
            .orElseThrow(() -> new BadRequestException("Company not found"));

    // 2. Validate email
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email already exists");
    }

    // 3. Create and save the User
    User user = clientMapper.toEntity(request, company, passwordEncoder);

    // upload image
    if (image != null && !image.isEmpty()) {
      String fileName = uploadImage(image, request.getName());
      user.setImage(fileName);
    }

    userRepository.save(user);

    // 4. Create and save the Client
    Client client = clientMapper.toClient(request);
    client.setCompany(company);
    client.setUser(user);

    clientRepository.save(client);

    // 5. Return the response
    return clientMapper.toResponse(client);
  }

  @Override
  public ClientResponse getClientById(Long id) {
    Client client =
        clientRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

    return clientMapper.toResponse(client);
  }

  @Override
  public List<ClientResponse> getAllClients() {
    List<Client> clients = clientRepository.findAll();
    if (clients.isEmpty()) {
      throw new ResourceNotFoundException("No clients found");
    }

    return clients.stream().map(clientMapper::toResponse).collect(Collectors.toList());
  }

  @Override
  public List<ClientResponse> getClientsByCompanyId(Long companyId) {
    List<Client> clients = clientRepository.findByCompanyId(companyId);
    if (clients.isEmpty()) {
      throw new ResourceNotFoundException("No clients found for company id: " + companyId);
    }

    return clients.stream().map(clientMapper::toResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteClient(Long id) {
    Client client =
        clientRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

    // Delete the associated User entity as well
    if (client.getUser() != null) {
      userRepository.delete(client.getUser());
    }

    clientRepository.delete(client);
  }

  private String uploadImage(MultipartFile file, String name) {
    try {
      Path path = Paths.get(uploadDir, "client");

      if (!Files.exists(path)) {
        Files.createDirectories(path);
      }

      String ext = "";
      String original = file.getOriginalFilename();

      if (original != null && original.contains(".")) {
        ext = original.substring(original.lastIndexOf("."));
      }

      String fileName = name.trim().replaceAll("\\s+", "_") + "_" + UUID.randomUUID() + ext;

      Files.copy(file.getInputStream(), path.resolve(fileName));

      return fileName;

    } catch (Exception e) {
      throw new RuntimeException("Image upload failed");
    }
  }
}
