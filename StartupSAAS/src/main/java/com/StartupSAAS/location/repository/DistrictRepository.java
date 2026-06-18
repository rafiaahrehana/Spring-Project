package com.StartupSAAS.location.repository;

import com.StartupSAAS.location.entity.District;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

  Optional<District> findByIdAndDivisionId(Long id, Long divisionId);

    List<District> findByDivisionId(Long divisionId);
}
