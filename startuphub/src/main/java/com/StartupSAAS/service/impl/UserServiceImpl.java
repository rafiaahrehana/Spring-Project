package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.UserMapper;
import com.StartupSAAS.dto.request.UserRequestDTO;
import com.StartupSAAS.dto.response.UserResponseDTO;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.UserService;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Transactional
    @Override
    public UserResponseDTO create(UserRequestDTO dto, MultipartFile profilePicture) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use: " + dto.getEmail());
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail().toLowerCase().trim());
        user.setPassword(dto.getPassword()); // will be BCrypt encoded in security layer
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setLanguagePref(dto.getLanguagePref() != null ? dto.getLanguagePref() : "EN");
        user.setIsActive(true);
        user.setEmailEnabled(true);
        user.setSmsEnabled(false);

        // Link to company if not SUPER_ADMIN
        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException(
                            "Company not found with id: " + dto.getCompanyId()));
            user.setCompany(company);
        }

        if (profilePicture != null && !profilePicture.isEmpty()) {
            user.setProfilePictureUrl(
                    uploadImage(profilePicture, dto.getFirstName(), "user"));
        }

        User saved = userRepository.save(user);
        return UserMapper.toDTO(
                userRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll()
                .stream().map(UserMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long id) {
        return UserMapper.toDTO(
                userRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new RuntimeException(
                                "User not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getByCompany(Long companyId) {
        return userRepository.findByCompanyId(companyId)
                .stream().map(UserMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getByCompanyAndRole(Long companyId, Role role) {
        return userRepository.findByCompanyIdAndRole(companyId, role)
                .stream().map(UserMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserResponseDTO update(Long id, UserRequestDTO dto, MultipartFile profilePicture) {

        User user = userRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + id));

        if (dto.getFirstName() != null)    user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null)     user.setLastName(dto.getLastName());
        if (dto.getPhone() != null)        user.setPhone(dto.getPhone());
        if (dto.getLanguagePref() != null) user.setLanguagePref(dto.getLanguagePref());

        // Email change: validate uniqueness
        if (dto.getEmail() != null
                && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already in use: " + dto.getEmail());
            }
            user.setEmail(dto.getEmail().toLowerCase().trim());
        }

        if (profilePicture != null && !profilePicture.isEmpty()) {
            user.setProfilePictureUrl(
                    uploadImage(profilePicture, user.getFirstName(), "user"));
        }

        User saved = userRepository.save(user);
        return UserMapper.toDTO(
                userRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
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
