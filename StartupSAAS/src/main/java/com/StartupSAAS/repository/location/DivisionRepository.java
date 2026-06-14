package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.Country;
import com.StartupSAAS.entity.address.Division;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DivisionRepository extends JpaRepository<Division, Long> {
    Optional<Division> findByNameAndCountry(String name, Country country);
}
