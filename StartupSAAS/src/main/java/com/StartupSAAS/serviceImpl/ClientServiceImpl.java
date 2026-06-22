package com.StartupSAAS.serviceImpl;

import com.StartupSAAS.dto.mapper.ClientMapper;
import com.StartupSAAS.dto.request.ClientRequest;
import com.StartupSAAS.dto.response.ClientResponse;
import com.StartupSAAS.entity.Client;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.location.entity.*;
import com.StartupSAAS.location.repository.*;
import com.StartupSAAS.repository.ClientRepository;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.ClientService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
  private final ImageService imageService;
  private final CountryRepository countryRepository;
  private final DivisionRepository divisionRepository;
  private final DistrictRepository districtRepository;
  private final PoliceStationRepository policeStationRepository;

  @Override
  @Transactional
  public ClientResponse saveClient(Long companyId, ClientRequest request, MultipartFile image) {
    Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new BadRequestException("Company not found"));

    if (userRepository.existsByEmail(request.getEmail()))
      throw new BadRequestException("Email already exists");

    Address address = null;
      if (request.getPoliceStationId() != null) {
        PoliceStation policeStation = policeStationRepository.findById(request.getPoliceStationId())
                .orElseThrow(() -> new BadRequestException("Police station not found"));

      address = new Address();
      address.setHouseNo(request.getHouseNo());
      address.setRoad(request.getRoad());
      address.setPostOffice(request.getPostOffice());
      address.setPoliceStation(policeStation);
    }

    User user = clientMapper.toUser(request, passwordEncoder);
    user.setAddress(address);
    if (image != null && !image.isEmpty())
      user.setImage(imageService.upload(image, "client", request.getName()));
    userRepository.save(user);

    Client client = clientMapper.toClient(request);
    client.setCompany(company);
    client.setUser(user);
    clientRepository.save(client);

    return clientMapper.toResponse(client);
  }

  @Override
  public ClientResponse getClientById(Long id) {
    return clientMapper.toResponse(
            clientRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id)));
  }

  @Override
  @Transactional
  public ClientResponse updateClient(Long id, ClientRequest request, MultipartFile image) {
    Client client = clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

    User user = client.getUser();
    user.setPhone(request.getPhone());
    if (image != null && !image.isEmpty())
      user.setImage(imageService.upload(image, "client", request.getName()));

    client.setBillingAddress(request.getBillingAddress());

    return clientMapper.toResponse(client);
  }

  @Override
  public List<ClientResponse> getAllClients() {
    return clientRepository.findAll().stream()
            .map(clientMapper::toResponse)
            .collect(Collectors.toList());
  }

  @Override
  public List<ClientResponse> getClientsByCompanyId(Long companyId) {
    List<Client> clients = clientRepository.findByCompanyId(companyId);
    if (clients.isEmpty())
      throw new ResourceNotFoundException("No clients found for company id: " + companyId);
    return clients.stream()
            .map(clientMapper::toResponse)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteClient(Long id) {
    Client client = clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    if (client.getUser() != null)
      userRepository.delete(client.getUser());
    clientRepository.delete(client);
  }
}