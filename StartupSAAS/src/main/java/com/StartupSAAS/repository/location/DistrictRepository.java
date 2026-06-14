package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.District;
import com.StartupSAAS.entity.address.Division;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {

    Optional<District> findByNameAndDivision(String name, Division division);
}
