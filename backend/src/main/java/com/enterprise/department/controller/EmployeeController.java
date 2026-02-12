package com.enterprise.department.controller;

import com.enterprise.department.dto.EmployeeDTO;
import com.enterprise.department.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Employee Controller
 * REST endpoints for employee management
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employees", description = "Employee management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Get all employees
     * All authenticated users can access
     */
    @GetMapping("/employees")
    @Operation(summary = "Get all employees", description = "Retrieve all employees")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        log.info("GET /api/v1/employees - Fetching all employees");

        List<EmployeeDTO> employees = employeeService.getAllEmployees();

        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees by department
     * All authenticated users can access
     */
    @GetMapping("/departments/{deptId}/employees")
    @Operation(summary = "Get employees by department", description = "Retrieve employees by department ID")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByDepartment(@PathVariable String deptId) {
        log.info("GET /api/v1/departments/{}/employees - Fetching employees", deptId);

        List<EmployeeDTO> employees = employeeService.getEmployeesByDepartment(deptId);

        return ResponseEntity.ok(employees);
    }

    /**
     * Get employee by ID
     * All authenticated users can access
     */
    @GetMapping("/employees/{empId}")
    @Operation(summary = "Get employee by ID", description = "Retrieve employee by ID")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable String empId) {
        log.info("GET /api/v1/employees/{} - Fetching employee", empId);

        EmployeeDTO employee = employeeService.getEmployeeById(empId);

        return ResponseEntity.ok(employee);
    }

    /**
     * Create new employee
     * ADMIN and HR can access
     */
    @PostMapping("/departments/{deptId}/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Create employee", description = "Create new employee in department (Admin/HR only)")
    public ResponseEntity<EmployeeDTO> createEmployee(
            @PathVariable String deptId,
            @Valid @RequestBody EmployeeDTO employeeDTO) {

        log.info("POST /api/v1/departments/{}/employees - Creating employee: {}", deptId, employeeDTO.getName());

        EmployeeDTO createdEmployee = employeeService.createEmployee(deptId, employeeDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    /**
     * Update employee
     * ADMIN and HR can access
     */
    @PutMapping("/departments/{deptId}/employees/{empId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Update employee", description = "Update existing employee (Admin/HR only)")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable String deptId,
            @PathVariable String empId,
            @Valid @RequestBody EmployeeDTO employeeDTO) {

        log.info("PUT /api/v1/departments/{}/employees/{} - Updating employee", deptId, empId);

        EmployeeDTO updatedEmployee = employeeService.updateEmployee(deptId, empId, employeeDTO);

        return ResponseEntity.ok(updatedEmployee);
    }

    /**
     * Delete employee
     * ADMIN only
     */
    @DeleteMapping("/departments/{deptId}/employees/{empId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete employee", description = "Delete employee (Admin only)")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable String deptId,
            @PathVariable String empId) {

        log.info("DELETE /api/v1/departments/{}/employees/{} - Deleting employee", deptId, empId);

        employeeService.deleteEmployee(deptId, empId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Group employees by department
     * All authenticated users can access
     */
    @GetMapping("/employees/grouped")
    @Operation(summary = "Group employees by department", description = "Get employees grouped by department")
    public ResponseEntity<Map<String, List<EmployeeDTO>>> groupEmployeesByDepartment() {
        log.info("GET /api/v1/employees/grouped - Grouping employees by department");

        Map<String, List<EmployeeDTO>> groupedEmployees = employeeService.groupEmployeesByDepartment();

        return ResponseEntity.ok(groupedEmployees);
    }

    /**
     * Calculate total salary by department
     * ADMIN and HR can access
     */
    @GetMapping("/employees/salary-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Calculate salary by department", description = "Get total salary by department (Admin/HR only)")
    public ResponseEntity<Map<String, Double>> calculateTotalSalaryByDepartment() {
        log.info("GET /api/v1/employees/salary-summary - Calculating salary by department");

        Map<String, Double> salaryByDepartment = employeeService.calculateTotalSalaryByDepartment();

        return ResponseEntity.ok(salaryByDepartment);
    }
}
