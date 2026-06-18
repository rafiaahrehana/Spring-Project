package com.StartupSAAS.location.repository;

import com.StartupSAAS.location.entity.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {
    Optional<PostOffice> findByIdAndPoliceStationId(Long id, Long policeStationId);

    List<PostOffice> findByPoliceStationId(Long policeStationId);
}
