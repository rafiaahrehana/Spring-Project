package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.PoliceStation;
import com.StartupSAAS.entity.address.PostOffice;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {
  Optional<PostOffice> findByNameAndPoliceStation(String name, PoliceStation policeStation);

  Optional<PostOffice> findByName(String name);
}
