package com.enterprise.department.service;

import com.enterprise.department.dto.EmployeeDTO;

import java.util.List;
import java.util.Map;

/**
 * Employee Service Interface
 */
public interface EmployeeService {

    /**
     * Get all employees
     *
     * @return List of employees
     */
    List<EmployeeDTO> getAllEmployees();

    /**
     * Get employees by department ID
     *
     * @param departmentId Department ID
     * @return List of employees
     */
    List<EmployeeDTO> getEmployeesByDepartment(String departmentId);

    /**
     * Get employee by ID
     *
     * @param id Employee ID
     * @return EmployeeDTO
     */
    EmployeeDTO getEmployeeById(String id);

    /**
     * Create new employee
     *
     * @param departmentId Department ID
     * @param employeeDTO  Employee data
     * @return Created employee
     */
    EmployeeDTO createEmployee(String departmentId, EmployeeDTO employeeDTO);

    /**
     * Update existing employee
     *
     * @param departmentId Department ID
     * @param employeeId   Employee ID
     * @param employeeDTO  Updated employee data
     * @return Updated employee
     */
    EmployeeDTO updateEmployee(String departmentId, String employeeId, EmployeeDTO employeeDTO);

    /**
     * Delete employee
     *
     * @param departmentId Department ID
     * @param employeeId   Employee ID
     */
    void deleteEmployee(String departmentId, String employeeId);

    /**
     * Group employees by department
     * Demonstrates use of Maps and Lists
     *
     * @return Map of department ID to list of employees
     */
    Map<String, List<EmployeeDTO>> groupEmployeesByDepartment();

    /**
     * Calculate total salary by department
     * Demonstrates use of Streams API
     *
     * @return Map of department ID to total salary
     */
    Map<String, Double> calculateTotalSalaryByDepartment();
}
