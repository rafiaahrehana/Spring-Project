package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.District;
import com.StartupSAAS.entity.address.PoliceStation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoliceStationRepository extends JpaRepository<PoliceStation, Long> {

  Optional<PoliceStation> findByIdAndDistrictId(Long id, Long districtId);
}
