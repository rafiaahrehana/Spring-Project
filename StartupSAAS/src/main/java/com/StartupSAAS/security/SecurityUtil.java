package com.StartupSAAS.security;

import com.StartupSAAS.entity.User;
import com.StartupSAAS.exception.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

  public User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof User user)
      return user;
    throw new BadRequestException("No authenticated user found");
  }
}