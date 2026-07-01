package com.startuphub.repository;

import com.startuphub.entity.LeaveBalance;
import com.startuphub.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    List<LeaveBalance> findByEmployeeIdAndYear(Long employeeId, int year);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeAndYear(
        Long employeeId, LeaveType leaveType, int year);

    List<LeaveBalance> findByCompanyIdAndYear(Long companyId, int year);
}
