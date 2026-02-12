package com.enterprise.department.service;

import com.enterprise.department.dto.DepartmentDTO;

import java.util.List;

/**
 * Department Service Interface
 */
public interface DepartmentService {

    /**
     * Get all departments
     *
     * @return List of departments
     */
    List<DepartmentDTO> getAllDepartments();

    /**
     * Get department by ID
     *
     * @param id Department ID
     * @return DepartmentDTO
     */
    DepartmentDTO getDepartmentById(String id);

    /**
     * Create new department
     *
     * @param departmentDTO Department data
     * @return Created department
     */
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);

    /**
     * Update existing department
     *
     * @param id            Department ID
     * @param departmentDTO Updated department data
     * @return Updated department
     */
    DepartmentDTO updateDepartment(String id, DepartmentDTO departmentDTO);

    /**
     * Delete department
     *
     * @param id Department ID
     */
    void deleteDepartment(String id);
}
