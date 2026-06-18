package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.ClientMapper;
import com.StartupSAAS.dto.request.ClientRequestDTO;
import com.StartupSAAS.dto.response.ClientResponseDTO;
import com.StartupSAAS.entity.Client;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.address.Address;
import com.StartupSAAS.entity.address.PostOffice;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.repository.ClientRepository;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.repository.location.AddressRepository;
import com.StartupSAAS.repository.location.PostOfficeRepository;
import com.StartupSAAS.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PostOfficeRepository postOfficeRepository;
    private final AddressRepository addressRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Transactional
    @Override
    public ClientResponseDTO create(ClientRequestDTO dto, MultipartFile image) {

        // 1. Validate company
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + dto.getCompanyId()));

        // 2. Validate email
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use: " + dto.getEmail());
        }

        // 3. Create User account with CLIENT role
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail().toLowerCase().trim());
        user.setPassword(dto.getPassword()); // BCrypt in security layer
        user.setPhone(dto.getPhone());
        user.setRole(Role.CLIENT);
        user.setCompany(company);
        user.setIsActive(true);
        user.setEmailEnabled(true);
        user.setSmsEnabled(false);

        User savedUser = userRepository.save(user);

        // 4. Build Address from postOfficeId + street
        Address address = null;
        if (dto.getPostOfficeId() != null && dto.getStreet() != null) {
            PostOffice postOffice = postOfficeRepository.findByIdWithDetails(dto.getPostOfficeId())
                    .orElseThrow(() -> new RuntimeException(
                            "PostOffice not found with id: " + dto.getPostOfficeId()));

            address = new Address();
            address.setStreet(dto.getStreet());
            address.setPostOffice(postOffice);
            address = addressRepository.save(address);
        }

        // 5. Create Client profile
        Client client = new Client();
        client.setUser(savedUser);
        client.setCompany(company);
        client.setContactPerson(dto.getContactPerson());
        client.setAddress(address);
        client.setActive(true);

        if (image != null && !image.isEmpty()) {
            client.setImage(uploadImage(image, dto.getFirstName(), "client"));
        }

        Client saved = clientRepository.save(client);
        return ClientMapper.toDTO(
                clientRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> getAll() {
        return clientRepository.findAllWithDetails()
                .stream().map(ClientMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO getById(Long id) {
        return ClientMapper.toDTO(
                clientRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Client not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> getByCompany(Long companyId) {
        return clientRepository.findByCompanyId(companyId)
                .stream().map(ClientMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ClientResponseDTO update(Long id, ClientRequestDTO dto, MultipartFile image) {

        Client client = clientRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "Client not found with id: " + id));

        // Update User fields
        User user = client.getUser();
        if (dto.getFirstName() != null)    user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null)     user.setLastName(dto.getLastName());
        if (dto.getPhone() != null)        user.setPhone(dto.getPhone());

        if (dto.getEmail() != null
                && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already in use: " + dto.getEmail());
            }
            user.setEmail(dto.getEmail().toLowerCase().trim());
        }
        userRepository.save(user);

        // Update Client profile
        if (dto.getContactPerson() != null) client.setContactPerson(dto.getContactPerson());

        // Update address
        if (dto.getPostOfficeId() != null && dto.getStreet() != null) {
            PostOffice postOffice = postOfficeRepository.findByIdWithDetails(dto.getPostOfficeId())
                    .orElseThrow(() -> new RuntimeException(
                            "PostOffice not found with id: " + dto.getPostOfficeId()));

            Address address = client.getAddress();
            if (address == null) address = new Address();
            address.setStreet(dto.getStreet());
            address.setPostOffice(postOffice);
            client.setAddress(addressRepository.save(address));
        }

        // Reassign company if changed
        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            client.setCompany(company);
        }

        if (image != null && !image.isEmpty()) {
            client.setImage(uploadImage(image, user.getFirstName(), "client"));
        }

        Client saved = clientRepository.save(client);
        return ClientMapper.toDTO(
                clientRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    public void delete(Long id) {
        clientRepository.deleteById(id);
    }

    private String uploadImage(MultipartFile file, String name, String folder) {
        try {
            Path path = Paths.get(uploadDir, folder);
            if (!Files.exists(path)) Files.createDirectories(path);

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains("."))
                ext = original.substring(original.lastIndexOf("."));

            String fileName = name.trim().replaceAll("\\s+", "_")
                    + "_" + UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }
}
