package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.ClientRequest;
import com.StartupSAAS.dto.response.ClientResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ClientService {
  ClientResponse saveClient(Long companyId, ClientRequest request, MultipartFile image);

  ClientResponse getClientById(Long id);

  List<ClientResponse> getAllClients();

  List<ClientResponse> getClientsByCompanyId(Long companyId);

  void deleteClient(Long id);
}
