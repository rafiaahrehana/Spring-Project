package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByDesignation(Designation designation);

    boolean existsByCompanyId(Long companyId);

    Collection<Object> findByCompanyId(Long companyId);
}
