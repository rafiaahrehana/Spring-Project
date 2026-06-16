package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Client;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByCompanyId(Long companyId);
    Optional<Client> findByUserId(Long userId);
    boolean existsByCompanyId(Long companyId);
}