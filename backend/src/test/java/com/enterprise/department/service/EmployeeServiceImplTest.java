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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmployeeServiceImpl
 * Tests employee management, validation, and business logic
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private EmployeeIdGenerator employeeIdGenerator;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee testEmployee;
    private EmployeeDTO testEmployeeDTO;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = new Department();
        testDepartment.setId("dept-123");
        testDepartment.setName("IT Department");
        testDepartment.setLocation("Building A");

        testEmployee = new Employee();
        testEmployee.setId("EMP001");
        testEmployee.setName("John Doe");
        testEmployee.setEmail("john.doe@example.com");
        testEmployee.setPosition("Software Engineer");
        testEmployee.setSalary(75000.0);
        testEmployee.setDepartment(testDepartment);

        testEmployeeDTO = new EmployeeDTO();
        testEmployeeDTO.setId("EMP001");
        testEmployeeDTO.setName("John Doe");
        testEmployeeDTO.setEmail("john.doe@example.com");
        testEmployeeDTO.setPosition("Software Engineer");
        testEmployeeDTO.setSalary(75000.0);
        testEmployeeDTO.setDepartmentId("dept-123");
    }

    @Test
    void getAllEmployees_ShouldReturnAllEmployees() {
        // Arrange
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findAll()).thenReturn(employees);
        when(employeeMapper.toDTO(any(Employee.class))).thenReturn(testEmployeeDTO);

        // Act
        List<EmployeeDTO> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());

        verify(employeeRepository).findAll();
        verify(employeeMapper, times(1)).toDTO(any(Employee.class));
    }

    @Test
    void getEmployeesByDepartment_WithValidDepartmentId_ShouldReturnEmployees() {
        // Arrange
        when(departmentRepository.existsById("dept-123")).thenReturn(true);
        when(employeeRepository.findByDepartmentId("dept-123")).thenReturn(Arrays.asList(testEmployee));
        when(employeeMapper.toDTO(any(Employee.class))).thenReturn(testEmployeeDTO);

        // Act
        List<EmployeeDTO> result = employeeService.getEmployeesByDepartment("dept-123");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("dept-123", result.get(0).getDepartmentId());

        verify(departmentRepository).existsById("dept-123");
        verify(employeeRepository).findByDepartmentId("dept-123");
    }

    @Test
    void getEmployeesByDepartment_WithInvalidDepartmentId_ShouldThrowException() {
        // Arrange
        when(departmentRepository.existsById("invalid-id")).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> employeeService.getEmployeesByDepartment("invalid-id"));

        verify(departmentRepository).existsById("invalid-id");
        verify(employeeRepository, never()).findByDepartmentId(anyString());
    }

    @Test
    void getEmployeeById_WithValidId_ShouldReturnEmployee() {
        // Arrange
        when(employeeRepository.findById("EMP001")).thenReturn(Optional.of(testEmployee));
        when(employeeMapper.toDTO(testEmployee)).thenReturn(testEmployeeDTO);

        // Act
        EmployeeDTO result = employeeService.getEmployeeById("EMP001");

        // Assert
        assertNotNull(result);
        assertEquals("EMP001", result.getId());
        assertEquals("John Doe", result.getName());

        verify(employeeRepository).findById("EMP001");
        verify(employeeMapper).toDTO(testEmployee);
    }

    @Test
    void getEmployeeById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(employeeRepository.findById("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> employeeService.getEmployeeById("INVALID"));

        verify(employeeRepository).findById("INVALID");
        verify(employeeMapper, never()).toDTO(any(Employee.class));
    }

    @Test
    void createEmployee_WithValidData_ShouldCreateEmployee() {
        // Arrange
        when(departmentRepository.findById("dept-123")).thenReturn(Optional.of(testDepartment));
        when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(employeeMapper.toEntity(testEmployeeDTO)).thenReturn(testEmployee);
        when(employeeIdGenerator.generateNextId()).thenReturn("EMP002");
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        when(employeeMapper.toDTO(testEmployee)).thenReturn(testEmployeeDTO);

        // Act
        EmployeeDTO result = employeeService.createEmployee("dept-123", testEmployeeDTO);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());

        verify(departmentRepository).findById("dept-123");
        verify(employeeRepository).existsByEmail("john.doe@example.com");
        verify(employeeIdGenerator).generateNextId();
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_WithDuplicateEmail_ShouldThrowException() {
        // Arrange
        when(departmentRepository.findById("dept-123")).thenReturn(Optional.of(testDepartment));
        when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEmailException.class,
                () -> employeeService.createEmployee("dept-123", testEmployeeDTO));

        verify(employeeRepository).existsByEmail("john.doe@example.com");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployee_WithInvalidDepartment_ShouldThrowException() {
        // Arrange
        when(departmentRepository.findById("invalid-dept")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> employeeService.createEmployee("invalid-dept", testEmployeeDTO));

        verify(departmentRepository).findById("invalid-dept");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployee_WithValidData_ShouldUpdateEmployee() {
        // Arrange
        EmployeeDTO updateDTO = new EmployeeDTO();
        updateDTO.setName("John Updated");
        updateDTO.setEmail("john.doe@example.com"); // Same email as existing
        updateDTO.setPosition("Senior Engineer");
        updateDTO.setSalary(85000.0);

        when(departmentRepository.existsById("dept-123")).thenReturn(true);
        when(employeeRepository.findById("EMP001")).thenReturn(Optional.of(testEmployee));
        // Email is same, so existsByEmail won't be called
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(employeeMapper.toDTO(testEmployee)).thenReturn(testEmployeeDTO);

        // Act
        EmployeeDTO result = employeeService.updateEmployee("dept-123", "EMP001", updateDTO);

        // Assert
        assertNotNull(result);
        verify(employeeRepository).findById("EMP001");
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    void updateEmployee_WithDuplicateEmail_ShouldThrowException() {
        // Arrange
        EmployeeDTO updateDTO = new EmployeeDTO();
        updateDTO.setEmail("existing@example.com"); // Different email from testEmployee

        when(departmentRepository.existsById("dept-123")).thenReturn(true);
        when(employeeRepository.findById("EMP001")).thenReturn(Optional.of(testEmployee));
        // Email is different, so check if it exists
        when(employeeRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEmailException.class,
                () -> employeeService.updateEmployee("dept-123", "EMP001", updateDTO));

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_WithValidId_ShouldDeleteEmployee() {
        // Arrange
        when(departmentRepository.findById("dept-123")).thenReturn(Optional.of(testDepartment));
        when(employeeRepository.findById("EMP001")).thenReturn(Optional.of(testEmployee));
        doNothing().when(employeeRepository).delete(testEmployee);

        // Act
        employeeService.deleteEmployee("dept-123", "EMP001");

        // Assert
        verify(employeeRepository).findById("EMP001");
        verify(employeeRepository).delete(testEmployee);
    }

    @Test
    void deleteEmployee_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(departmentRepository.findById("dept-123")).thenReturn(Optional.of(testDepartment));
        when(employeeRepository.findById("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> employeeService.deleteEmployee("dept-123", "INVALID"));

        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    void groupEmployeesByDepartment_ShouldReturnGroupedMap() {
        // Arrange
        Employee emp2 = new Employee();
        emp2.setId("EMP002");
        emp2.setName("Jane Smith");
        emp2.setDepartment(testDepartment);

        when(employeeRepository.findAll()).thenReturn(Arrays.asList(testEmployee, emp2));
        when(employeeMapper.toDTO(any(Employee.class))).thenReturn(testEmployeeDTO);

        // Act
        Map<String, List<EmployeeDTO>> result = employeeService.groupEmployeesByDepartment();

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("IT Department"));
        assertEquals(2, result.get("IT Department").size());

        verify(employeeRepository).findAll();
    }

    @Test
    void calculateTotalSalaryByDepartment_ShouldReturnSalaryMap() {
        // Arrange
        Employee emp2 = new Employee();
        emp2.setId("EMP002");
        emp2.setSalary(85000.0);
        emp2.setDepartment(testDepartment);

        when(employeeRepository.findAll()).thenReturn(Arrays.asList(testEmployee, emp2));

        // Act
        Map<String, Double> result = employeeService.calculateTotalSalaryByDepartment();

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("IT Department"));
        assertEquals(160000.0, result.get("IT Department"));

        verify(employeeRepository).findAll();
    }

    @Test
    void calculateTotalSalaryByDepartment_WithNoEmployees_ShouldReturnEmptyMap() {
        // Arrange
        when(employeeRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        Map<String, Double> result = employeeService.calculateTotalSalaryByDepartment();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository).findAll();
    }

    @Test
    void createEmployee_ShouldGenerateUniqueId() {
        // Arrange
        when(departmentRepository.findById("dept-123")).thenReturn(Optional.of(testDepartment));
        when(employeeRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(employeeMapper.toEntity(any(EmployeeDTO.class))).thenReturn(testEmployee);
        when(employeeIdGenerator.generateNextId()).thenReturn("EMP003");
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        when(employeeMapper.toDTO(any(Employee.class))).thenReturn(testEmployeeDTO);

        EmployeeDTO newEmployeeDTO = new EmployeeDTO();
        newEmployeeDTO.setName("New Employee");
        newEmployeeDTO.setEmail("new@example.com");
        newEmployeeDTO.setPosition("Developer");
        newEmployeeDTO.setSalary(70000.0);

        // Act
        employeeService.createEmployee("dept-123", newEmployeeDTO);

        // Assert
        verify(employeeIdGenerator).generateNextId();
    }
}
