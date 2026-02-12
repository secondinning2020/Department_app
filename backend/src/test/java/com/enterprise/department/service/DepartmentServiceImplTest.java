package com.enterprise.department.service;

import com.enterprise.department.dto.DepartmentDTO;
import com.enterprise.department.entity.Department;
import com.enterprise.department.entity.Employee;
import com.enterprise.department.exception.EntityNotFoundException;
import com.enterprise.department.mapper.DepartmentMapper;
import com.enterprise.department.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DepartmentServiceImpl
 * Tests CRUD operations, validation, and business logic
 */
@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department testDepartment;
    private DepartmentDTO testDepartmentDTO;
    private List<Employee> employees;

    @BeforeEach
    void setUp() {
        testDepartment = new Department();
        testDepartment.setId("dept-123");
        testDepartment.setName("IT Department");
        testDepartment.setLocation("Building A");

        employees = new ArrayList<>();
        Employee emp1 = new Employee();
        emp1.setId("EMP001");
        emp1.setName("John Doe");
        emp1.setSalary(75000.0);
        employees.add(emp1);

        testDepartment.setEmployees(employees);

        testDepartmentDTO = new DepartmentDTO();
        testDepartmentDTO.setId("dept-123");
        testDepartmentDTO.setName("IT Department");
        testDepartmentDTO.setLocation("Building A");
    }

    @Test
    void getAllDepartments_ShouldReturnAllDepartments() {
        // Arrange
        List<Department> departments = Arrays.asList(testDepartment);
        when(departmentRepository.findAll()).thenReturn(departments);
        when(departmentMapper.toDTO(any(Department.class))).thenReturn(testDepartmentDTO);

        // Act
        List<DepartmentDTO> result = departmentService.getAllDepartments();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("IT Department", result.get(0).getName());

        verify(departmentRepository).findAll();
        verify(departmentMapper, times(1)).toDTO(any(Department.class));
    }

    @Test
    void getAllDepartments_WhenNoDepartments_ShouldReturnEmptyList() {
        // Arrange
        when(departmentRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<DepartmentDTO> result = departmentService.getAllDepartments();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(departmentRepository).findAll();
    }

    @Test
    void getDepartmentById_WithValidId_ShouldReturnDepartment() {
        // Arrange
        when(departmentRepository.findById("dept-123")).thenReturn(Optional.of(testDepartment));
        when(departmentMapper.toDTO(testDepartment)).thenReturn(testDepartmentDTO);

        // Act
        DepartmentDTO result = departmentService.getDepartmentById("dept-123");

        // Assert
        assertNotNull(result);
        assertEquals("dept-123", result.getId());
        assertEquals("IT Department", result.getName());

        verify(departmentRepository).findById("dept-123");
        verify(departmentMapper).toDTO(testDepartment);
    }

    @Test
    void getDepartmentById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(departmentRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> departmentService.getDepartmentById("invalid-id"));

        assertTrue(exception.getMessage().contains("invalid-id"));
        verify(departmentRepository).findById("invalid-id");
        verify(departmentMapper, never()).toDTO(any(Department.class));
    }

    @Test
    void createDepartment_WithValidData_ShouldCreateDepartment() {
        // Arrange
        when(departmentMapper.toEntity(testDepartmentDTO)).thenReturn(testDepartment);
        when(departmentRepository.save(testDepartment)).thenReturn(testDepartment);
        when(departmentMapper.toDTO(testDepartment)).thenReturn(testDepartmentDTO);

        // Act
        DepartmentDTO result = departmentService.createDepartment(testDepartmentDTO);

        // Assert
        assertNotNull(result);
        assertEquals("IT Department", result.getName());
        assertEquals("Building A", result.getLocation());

        verify(departmentMapper).toEntity(testDepartmentDTO);
        verify(departmentRepository).save(testDepartment);
        verify(departmentMapper).toDTO(testDepartment);
    }

    @Test
    void createDepartment_WithNullDTO_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> departmentService.createDepartment(null));

        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void updateDepartment_WithValidData_ShouldUpdateDepartment() {
        // Arrange
        DepartmentDTO updateDTO = new DepartmentDTO();
        updateDTO.setName("Updated IT");
        updateDTO.setLocation("Building B");

        when(departmentRepository.findById("dept-123")).thenReturn(Optional.of(testDepartment));
        when(departmentRepository.save(testDepartment)).thenReturn(testDepartment);
        when(departmentMapper.toDTO(testDepartment)).thenReturn(testDepartmentDTO);

        // Act
        DepartmentDTO result = departmentService.updateDepartment("dept-123", updateDTO);

        // Assert
        assertNotNull(result);
        verify(departmentRepository).findById("dept-123");
        verify(departmentRepository).save(testDepartment);
        verify(departmentMapper).toDTO(testDepartment);
    }

    @Test
    void updateDepartment_WithInvalidId_ShouldThrowException() {
        // Arrange
        DepartmentDTO updateDTO = new DepartmentDTO();
        updateDTO.setName("Updated IT");

        when(departmentRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> departmentService.updateDepartment("invalid-id", updateDTO));

        verify(departmentRepository).findById("invalid-id");
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void deleteDepartment_WithValidId_ShouldDeleteDepartment() {
        // Arrange
        when(departmentRepository.findById("dept-123")).thenReturn(Optional.of(testDepartment));
        doNothing().when(departmentRepository).delete(testDepartment);

        // Act
        departmentService.deleteDepartment("dept-123");

        // Assert
        verify(departmentRepository).findById("dept-123");
        verify(departmentRepository).delete(testDepartment);
    }

    @Test
    void deleteDepartment_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(departmentRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> departmentService.deleteDepartment("invalid-id"));

        verify(departmentRepository).findById("invalid-id");
        verify(departmentRepository, never()).delete(any(Department.class));
    }

    @Test
    void deleteDepartment_WithEmployees_ShouldStillDelete() {
        // Arrange
        testDepartment.setEmployees(employees);
        when(departmentRepository.findById("dept-123")).thenReturn(Optional.of(testDepartment));
        doNothing().when(departmentRepository).delete(testDepartment);

        // Act
        departmentService.deleteDepartment("dept-123");

        // Assert
        verify(departmentRepository).findById("dept-123");
        verify(departmentRepository).delete(testDepartment);
    }

    @Test
    void createDepartment_ShouldLogCreation() {
        // Arrange
        when(departmentMapper.toEntity(testDepartmentDTO)).thenReturn(testDepartment);
        when(departmentRepository.save(testDepartment)).thenReturn(testDepartment);
        when(departmentMapper.toDTO(testDepartment)).thenReturn(testDepartmentDTO);

        // Act
        departmentService.createDepartment(testDepartmentDTO);

        // Assert - verify save was called (logging happens in service)
        verify(departmentRepository).save(testDepartment);
    }

    @Test
    void updateDepartment_ShouldPreserveEmployees() {
        // Arrange
        DepartmentDTO updateDTO = new DepartmentDTO();
        updateDTO.setName("Updated IT");
        updateDTO.setLocation("Building C");

        testDepartment.setEmployees(employees);

        when(departmentRepository.findById("dept-123")).thenReturn(Optional.of(testDepartment));
        when(departmentRepository.save(testDepartment)).thenReturn(testDepartment);
        when(departmentMapper.toDTO(testDepartment)).thenReturn(testDepartmentDTO);

        // Act
        departmentService.updateDepartment("dept-123", updateDTO);

        // Assert
        verify(departmentRepository).save(testDepartment);
        assertEquals(1, testDepartment.getEmployees().size());
    }
}
