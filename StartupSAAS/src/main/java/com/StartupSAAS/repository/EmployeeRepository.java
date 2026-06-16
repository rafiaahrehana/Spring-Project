package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.enums.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

  List<Employee> findByDesignation(Designation designation);

  List<Employee> findByUser_Role(Role role);
}
