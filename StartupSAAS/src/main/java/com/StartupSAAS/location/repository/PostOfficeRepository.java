package com.StartupSAAS.location.repository;

import com.StartupSAAS.location.entity.Division;
import com.StartupSAAS.location.entity.PostOffice;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {
  Optional<PostOffice> findByIdAndPoliceStationId(Long id, Long policeStationId);

}
