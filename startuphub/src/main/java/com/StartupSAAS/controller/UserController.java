package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.UserRequestDTO;
import com.StartupSAAS.dto.response.UserResponseDTO;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST /api/users
    // multipart: "user" (JSON part) + "profilePicture" (file, optional)
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(
            @RequestPart("user") UserRequestDTO dto,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {
        return new ResponseEntity<>(userService.create(dto, profilePicture), HttpStatus.CREATED);
    }

    // GET /api/users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        List<UserResponseDTO> list = userService.getAll();
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    // GET /api/users/1
    @GetMapping("/{id}")
    public UserResponseDTO getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    // GET /api/users/company/3
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<UserResponseDTO>> getByCompany(
            @PathVariable Long companyId) {
        List<UserResponseDTO> list = userService.getByCompany(companyId);
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    // GET /api/users/company/3/role/EMPLOYEE
    @GetMapping("/company/{companyId}/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getByCompanyAndRole(
            @PathVariable Long companyId,
            @PathVariable String role) {
        Role userRole;
        try {
            userRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        List<UserResponseDTO> list = userService.getByCompanyAndRole(companyId, userRole);
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    // PUT /api/users/1
    @PutMapping("/{id}")
    public UserResponseDTO update(
            @PathVariable Long id,
            @RequestPart("user") UserRequestDTO dto,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {
        return userService.update(id, dto, profilePicture);
    }

    // DELETE /api/users/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
