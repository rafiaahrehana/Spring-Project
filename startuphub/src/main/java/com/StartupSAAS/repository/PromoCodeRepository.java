package com.StartupSAAS.repository;

import com.StartupSAAS.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCode(String code);
    List<PromoCode> findByCompanyId(Long companyId);
    List<PromoCode> findByCompanyIdAndActiveTrue(Long companyId);
}
