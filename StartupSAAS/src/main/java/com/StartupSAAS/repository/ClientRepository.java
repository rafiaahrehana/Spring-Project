package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Client;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
  List<Client> findByCompanyId(Long companyId);
}
