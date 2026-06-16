package com.StartupSAAS.location.repository;

import com.StartupSAAS.location.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {
    Optional<Division> findByIdAndCountryId(Long id, Long countryId);
}
