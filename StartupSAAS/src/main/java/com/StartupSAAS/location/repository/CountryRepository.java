package com.StartupSAAS.location.repository;

import com.StartupSAAS.location.entity.Country;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
  Optional<Country> findById(Long id);

}
