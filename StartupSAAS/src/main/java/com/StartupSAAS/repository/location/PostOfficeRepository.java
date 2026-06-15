package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.PoliceStation;
import com.StartupSAAS.entity.address.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.Optional;

@Repository
public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {
    Optional<PostOffice> findByNameAndPoliceStation(String name, PoliceStation policeStation);

}
