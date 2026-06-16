package com.StartupSAAS.location.repository;

import com.StartupSAAS.location.entity.PoliceStation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoliceStationRepository extends JpaRepository<PoliceStation, Long> {

  Optional<PoliceStation> findByIdAndDistrictId(Long id, Long districtId);
}
