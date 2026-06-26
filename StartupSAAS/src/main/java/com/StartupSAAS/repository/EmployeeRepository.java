package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.enums.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByCompanyId(Long companyId);

    List<Employee> findByCompanyId(Long companyId);

    List<Employee> findByCompanyIdAndDesignation(Long companyId, Designation designation);
}