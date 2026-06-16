package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.request.ClientRequest;
import com.StartupSAAS.dto.response.ClientResponse;
import com.StartupSAAS.entity.Client;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.location.mapper.AddressMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public User toUser(ClientRequest request, PasswordEncoder encoder) {
        User user = new User();
        user.setFirstName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.CLIENT);
        user.setActive(true);
        return user;
    }

    public Client toClient(ClientRequest request) {
        Client client = new Client();
        client.setBillingAddress(request.getBillingAddress());
        return client;
    }

    public ClientResponse toResponse(Client client) {
        ClientResponse response = new ClientResponse();
        response.setId(client.getId());
        response.setBillingAddress(client.getBillingAddress());

        if (client.getUser() != null) {
            User user = client.getUser();
            response.setName(user.getFirstName());
            response.setEmail(user.getEmail());
            response.setPhone(user.getPhone());
            response.setImage(user.getImage());

            if (user.getAddress() != null)
                response.setAddress(AddressMapper.toDTO(user.getAddress()));
        }

        if (client.getCompany() != null)
            response.setCompanyId(client.getCompany().getId());

        return response;
    }
}