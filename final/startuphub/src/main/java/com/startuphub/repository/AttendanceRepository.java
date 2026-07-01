package com.startuphub.repository;

import com.startuphub.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    Optional<Attendance> findByIdAndCompanyId(Long id, Long companyId);

    Page<Attendance> findByCompanyIdAndEmployeeId(Long companyId, Long employeeId, Pageable pageable);

    List<Attendance> findByCompanyIdAndEmployeeIdAndDateBetween(
        Long companyId, Long employeeId, LocalDate from, LocalDate to);

    long countByCompanyIdAndEmployeeIdAndPresentTrueAndDateBetween(
        Long companyId, Long employeeId, LocalDate from, LocalDate to);
}
