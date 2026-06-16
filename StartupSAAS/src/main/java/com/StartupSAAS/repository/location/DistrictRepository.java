package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.District;
import com.StartupSAAS.entity.address.Division;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<District, Long> {

  Optional<District> findByNameAndDivision(String name, Division division);
}
