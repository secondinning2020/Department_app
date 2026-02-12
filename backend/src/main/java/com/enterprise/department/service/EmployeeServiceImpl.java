package com.enterprise.department.service;

import com.enterprise.department.dto.EmployeeDTO;
import com.enterprise.department.entity.Department;
import com.enterprise.department.entity.Employee;
import com.enterprise.department.exception.DuplicateEmailException;
import com.enterprise.department.exception.EntityNotFoundException;
import com.enterprise.department.mapper.EmployeeMapper;
import com.enterprise.department.repository.DepartmentRepository;
import com.enterprise.department.repository.EmployeeRepository;
import com.enterprise.department.util.EmployeeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Employee Service Implementation
 * Handles business logic for employee operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeeIdGenerator employeeIdGenerator;

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        log.info("Fetching all employees");

        List<Employee> employees = employeeRepository.findAll();

        log.info("Found {} employees", employees.size());

        return employees.stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesByDepartment(String departmentId) {
        log.info("Fetching employees for department: {}", departmentId);

        // Verify department exists
        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException("Department", departmentId);
        }

        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId);

        log.info("Found {} employees in department {}", employees.size(), departmentId);

        return employees.stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getEmployeeById(String id) {
        log.info("Fetching employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee", id));

        log.info("Found employee: {}", employee.getName());

        return employeeMapper.toDTO(employee);
    }

    @Override
    @Transactional
    public EmployeeDTO createEmployee(String departmentId, EmployeeDTO employeeDTO) {
        log.info("Creating new employee: {} in department: {}", employeeDTO.getName(), departmentId);

        // Verify department exists
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department", departmentId));

        // Check for duplicate email
        if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new DuplicateEmailException(employeeDTO.getEmail());
        }

        // Create employee entity
        Employee employee = employeeMapper.toEntity(employeeDTO);

        // Generate custom employee ID
        String employeeId = employeeIdGenerator.generateNextId();
        employee.setId(employeeId);

        // Set department
        employee.setDepartment(department);

        // Save employee
        Employee savedEmployee = employeeRepository.save(employee);

        log.info("Employee created successfully with id: {}", savedEmployee.getId());

        return employeeMapper.toDTO(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployee(String departmentId, String employeeId, EmployeeDTO employeeDTO) {
        log.info("Updating employee with id: {} in department: {}", employeeId, departmentId);

        // Verify department exists
        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException("Department", departmentId);
        }

        // Find existing employee
        Employee existingEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId));

        // Verify employee belongs to the specified department
        if (!existingEmployee.getDepartment().getId().equals(departmentId)) {
            throw new IllegalArgumentException("Employee does not belong to the specified department");
        }

        // Check for duplicate email (excluding current employee)
        if (!existingEmployee.getEmail().equals(employeeDTO.getEmail()) &&
                employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new DuplicateEmailException(employeeDTO.getEmail());
        }

        // Update employee
        employeeMapper.updateEntityFromDTO(employeeDTO, existingEmployee);
        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        log.info("Employee updated successfully: {}", updatedEmployee.getName());

        return employeeMapper.toDTO(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(String departmentId, String employeeId) {
        log.info("Deleting employee with id: {} from department: {}", employeeId, departmentId);

        // Verify department exists
        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException("Department", departmentId);
        }

        // Find employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId));

        // Verify employee belongs to the specified department
        if (!employee.getDepartment().getId().equals(departmentId)) {
            throw new IllegalArgumentException("Employee does not belong to the specified department");
        }

        employeeRepository.delete(employee);

        log.info("Employee deleted successfully: {}", employee.getName());
    }

    @Override
    public Map<String, List<EmployeeDTO>> groupEmployeesByDepartment() {
        log.info("Grouping employees by department");

        List<Employee> employees = employeeRepository.findAll();

        Map<String, List<EmployeeDTO>> groupedEmployees = employees.stream()
                .collect(Collectors.groupingBy(
                        employee -> employee.getDepartment().getId(),
                        Collectors.mapping(employeeMapper::toDTO, Collectors.toList())));

        log.info("Grouped {} employees into {} departments", employees.size(), groupedEmployees.size());

        return groupedEmployees;
    }

    @Override
    public Map<String, Double> calculateTotalSalaryByDepartment() {
        log.info("Calculating total salary by department");

        List<Employee> employees = employeeRepository.findAll();

        Map<String, Double> salaryByDepartment = employees.stream()
                .collect(Collectors.groupingBy(
                        employee -> employee.getDepartment().getId(),
                        Collectors.summingDouble(Employee::getSalary)));

        log.info("Calculated salary for {} departments", salaryByDepartment.size());

        return salaryByDepartment;
    }
}
