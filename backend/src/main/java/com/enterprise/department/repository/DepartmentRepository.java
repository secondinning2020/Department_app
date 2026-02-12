package com.enterprise.department.repository;

import com.enterprise.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Department entity
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

    /**
     * Find department by name
     *
     * @param name Department name
     * @return Optional department
     */
    Optional<Department> findByName(String name);

    /**
     * Check if department exists by name
     *
     * @param name Department name
     * @return true if exists
     */
    boolean existsByName(String name);

    /**
     * Find department by location
     *
     * @param location Department location
     * @return Optional department
     */
    Optional<Department> findByLocation(String location);
}
