package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.UserResponse;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.location.mapper.AddressMapper;

public class UserMapper {

  public static UserResponse toDTO(User user) {
    UserResponse response = new UserResponse();
    response.setId(user.getId());
    response.setFirstName(user.getFirstName());
    response.setLastName(user.getLastName());
    response.setEmail(user.getEmail());
    response.setPhone(user.getPhone());
    response.setRole(user.getRole());
    response.setActive(user.isActive());
    response.setImage(user.getImage());
    response.setCreatedAt(user.getCreatedAt());

    if (user.getAddress() != null)
      response.setAddress(AddressMapper.toDTO(user.getAddress()));

    return response;
  }
}