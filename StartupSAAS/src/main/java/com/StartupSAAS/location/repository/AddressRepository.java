package com.StartupSAAS.location.repository;

import com.StartupSAAS.location.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
