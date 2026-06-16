package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.Country;
import com.StartupSAAS.entity.address.Division;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DivisionRepository extends JpaRepository<Division, Long> {
  Optional<Division> findByNameAndCountry(String name, Country country);
}
