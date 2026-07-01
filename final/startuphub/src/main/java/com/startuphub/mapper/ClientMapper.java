package com.startuphub.mapper;

import com.startuphub.dto.response.ClientResponse;
import com.startuphub.entity.Client;
import com.startuphub.entity.Employee;
import com.startuphub.entity.User;

public final class ClientMapper {

    private ClientMapper() {}

    public static ClientResponse toResponse(Client c) {
        User u = c.getUser();
        Employee am = c.getAccountManager();
        return new ClientResponse(
            c.getId(),
            u != null ? u.getId() : null,
            u != null ? u.getFirstName() : null,
            u != null ? u.getLastName() : null,
            u != null ? u.getEmail() : null,
            u != null ? u.getPhone() : null,
            u != null ? u.getImage() : null,
            c.getClientCompanyName(),
            c.getIndustry(),
            c.getWebsite(),
            c.getStatus(),
            c.isPortalAccessEnabled(),
            am != null ? am.getId() : null,
            am != null && am.getUser() != null ? am.getUser().getFullName() : null,
            c.getOnboardedAt(),
            c.getCreatedAt()
        );
    }
}
