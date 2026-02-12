package com.enterprise.department.repository;

import com.enterprise.department.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Employee entity
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    /**
     * Find employees by department ID
     *
     * @param departmentId Department ID
     * @return List of employees
     */
    List<Employee> findByDepartmentId(String departmentId);

    /**
     * Find employee by email
     *
     * @param email Employee email
     * @return Optional employee
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Check if employee exists by email
     *
     * @param email Employee email
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Find maximum employee ID for ID generation
     *
     * @return Maximum employee ID (e.g., "EMP025")
     */
    @Query("SELECT e.id FROM Employee e ORDER BY e.id DESC LIMIT 1")
    String findMaxEmployeeId();

    /**
     * Count employees in a department
     *
     * @param departmentId Department ID
     * @return Employee count
     */
    long countByDepartmentId(String departmentId);
}
