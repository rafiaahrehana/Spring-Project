package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.mapper.location.AddressMapper;
import com.StartupSAAS.dto.response.UserResponse;
import com.StartupSAAS.entity.User;

public class UserMapper {

  public static UserResponse toDTO(User user) {
    Long companyId = null;
    String companyName = null;

    if (user.getCompany() != null) {
      // If the user is a Company Owner
      companyId = user.getCompany().getId();
      companyName = user.getCompany().getName();
    } else if (user.getEmployee() != null && user.getEmployee().getCompany() != null) {
      // If the user is an Employee
      companyId = user.getEmployee().getCompany().getId();
      companyName = user.getEmployee().getCompany().getName();
    }

    return UserResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .role(user.getRole())
        .companyId(companyId)
        .companyName(companyName)
        .address(new AddressMapper().toDTO(user.getAddress()))
        .phone(user.getPhone())
        .profileImageUrl(user.getImage())
        .isActive(user.isActive())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
