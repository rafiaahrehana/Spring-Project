package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Single employee with full details:
    // user + company + address full chain
    @Query("""
        SELECT e FROM Employee e
        LEFT JOIN FETCH e.user
        LEFT JOIN FETCH e.company
        LEFT JOIN FETCH e.address a
        LEFT JOIN FETCH a.postOffice po
        LEFT JOIN FETCH po.policeStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
        WHERE e.id = :id
    """)
    Optional<Employee> findByIdWithDetails(@Param("id") Long id);

    // All employees with full details
    @Query("""
        SELECT e FROM Employee e
        LEFT JOIN FETCH e.user
        LEFT JOIN FETCH e.company
        LEFT JOIN FETCH e.address a
        LEFT JOIN FETCH a.postOffice po
        LEFT JOIN FETCH po.policeStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
    """)
    List<Employee> findAllWithDetails();

    // All employees in a company
    @Query("""
        SELECT e FROM Employee e
        LEFT JOIN FETCH e.user
        LEFT JOIN FETCH e.company
        LEFT JOIN FETCH e.address a
        LEFT JOIN FETCH a.postOffice po
        LEFT JOIN FETCH po.policeStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
        WHERE e.company.id = :companyId
    """)
    List<Employee> findByCompanyId(@Param("companyId") Long companyId);

    // Active employees in a company
    List<Employee> findByCompanyIdAndActiveTrue(Long companyId);

    boolean existsByUserId(Long userId);
}
