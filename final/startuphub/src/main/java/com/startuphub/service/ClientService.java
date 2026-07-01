package com.startuphub.service;

import com.startuphub.dto.request.CreateClientRequest;
import com.startuphub.dto.request.UpdateClientRequest;
import com.startuphub.dto.response.ClientResponse;
import com.startuphub.enums.ClientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    ClientResponse create(CreateClientRequest request);

    ClientResponse getById(Long id);

    ClientResponse getMyProfile();

    Page<ClientResponse> listAll(ClientStatus status, Pageable pageable);

    ClientResponse update(Long id, UpdateClientRequest request);

    void delete(Long id);
}
