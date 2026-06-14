package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.District;
import com.StartupSAAS.entity.address.PoliceStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PoliceStationRepository extends JpaRepository<PoliceStation, Long> {

    Optional<PoliceStation> findByNameAndDistrict(String name, District district);
}
