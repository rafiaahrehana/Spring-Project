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
  public ClientResponse saveClient(
          Long companyId, ClientRequest clientRequest, MultipartFile image) {

    Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new BadRequestException("Company not found"));

    if (userRepository.existsByEmail(clientRequest.getEmail())) {
      throw new BadRequestException("Email already exists");
    }

    User user = clientMapper.toEntity(clientRequest, company, passwordEncoder);
    if (image != null && !image.isEmpty()) {
      user.setImage(uploadImage(image, clientRequest.getName()));
    }
    userRepository.save(user);

    Client client = clientMapper.toClient(clientRequest);
    client.setCompany(company);
    client.setUser(user);
    clientRepository.save(client);
    return clientMapper.toResponse(client);
  }

  @Override
  public ClientResponse getClientById(Long id) {
    Client client = clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

    return clientMapper.toResponse(client);
  }
  @Override
  public List<ClientResponse> getClientsByCompanyId(Long companyId) {
    List<Client> clients = clientRepository.findByCompanyId(companyId);
    if (clients.isEmpty()) throw new ResourceNotFoundException("No clients found for company id: " + companyId);

    return clients.stream().map(clientMapper::toResponse).collect(Collectors.toList());
  }

  @Override
  public List<ClientResponse> getAllClients() {
    List<Client> clients = clientRepository.findAll();
    if (clients.isEmpty()) throw new ResourceNotFoundException("No Clients Found");

    return clients.stream().map(clientMapper::toResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteClient(Long id) {
    Client client = clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

    if (client.getUser() != null) {
      userRepository.delete(client.getUser());
    }
    clientRepository.delete(client);
  }

  private String uploadImage(MultipartFile file, String name) {
    try {
      Path path = Paths.get(uploadDir, "employee");
      if (!Files.exists(path)) Files.createDirectories(path);

      String original = file.getOriginalFilename();
      String ext = (original != null && original.contains(".")) ? original.substring(original.lastIndexOf(".")) : "";

      String fileName = name.trim().replaceAll("\\s+", "_") + "_" + UUID.randomUUID() + ext;
      Files.copy(file.getInputStream(), path.resolve(fileName));

      return fileName;

    } catch (Exception e) {
      throw new RuntimeException("Image upload failed");
    }
  }
}
