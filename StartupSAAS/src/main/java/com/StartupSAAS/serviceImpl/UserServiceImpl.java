package com.StartupSAAS.serviceImpl;

import com.StartupSAAS.dto.mapper.UserMapper;
import com.StartupSAAS.dto.request.PasswordChangeRequest;
import com.StartupSAAS.dto.request.UserRequest;
import com.StartupSAAS.dto.response.UserResponse;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.location.entity.Address;
import com.StartupSAAS.location.entity.PoliceStation;

import com.StartupSAAS.location.repository.PoliceStationRepository;

import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.UserService;
import com.StartupSAAS.security.SecurityUtil;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;
    private final PoliceStationRepository policeStationRepository;


    @Override
    public UserResponse getMyProfile() {
        return UserMapper.toDTO(securityUtil.getCurrentUser());
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(UserRequest request, MultipartFile image) {
        User user = securityUtil.getCurrentUser();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        if (image != null && !image.isEmpty())
            user.setImage(imageService.upload(image, "profile", request.getFirstName()));

        if (request.getPoliceStationId() != null) {
            PoliceStation policeStation = policeStationRepository.findById(request.getPoliceStationId())
                    .orElseThrow(() -> new BadRequestException("Police station not found"));

            Address address = user.getAddress() != null ? user.getAddress() : new Address();
            address.setHouseNo(request.getHouseNo());
            address.setRoad(request.getRoad());
            address.setPostOffice(request.getPostOffice());
            address.setPoliceStation(policeStation);
            user.setAddress(address);
        }

        return UserMapper.toDTO(user);
    }

    @Override
    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        User user = securityUtil.getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new BadRequestException("Current password is incorrect");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Override
    public UserResponse getUserById(Long id) {
        return UserMapper.toDTO(
                userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id)));
    }

    @Override
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleUserActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(!user.isActive());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
}