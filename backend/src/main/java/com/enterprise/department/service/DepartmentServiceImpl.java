package com.enterprise.department.service;

import com.enterprise.department.dto.DepartmentDTO;
import com.enterprise.department.entity.Department;
import com.enterprise.department.exception.EntityNotFoundException;
import com.enterprise.department.mapper.DepartmentMapper;
import com.enterprise.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Department Service Implementation
 * Handles business logic for department operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        log.info("Fetching all departments");

        List<Department> departments = departmentRepository.findAll();

        log.info("Found {} departments", departments.size());

        return departments.stream()
                .map(departmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDTO getDepartmentById(String id) {
        log.info("Fetching department with id: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department", id));

        log.info("Found department: {}", department.getName());

        return departmentMapper.toDTO(department);
    }

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        log.info("Creating new department: {}", departmentDTO.getName());

        Department department = departmentMapper.toEntity(departmentDTO);
        Department savedDepartment = departmentRepository.save(department);

        log.info("Department created successfully with id: {}", savedDepartment.getId());

        return departmentMapper.toDTO(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(String id, DepartmentDTO departmentDTO) {
        log.info("Updating department with id: {}", id);

        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department", id));

        departmentMapper.updateEntityFromDTO(departmentDTO, existingDepartment);
        Department updatedDepartment = departmentRepository.save(existingDepartment);

        log.info("Department updated successfully: {}", updatedDepartment.getName());

        return departmentMapper.toDTO(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(String id) {
        log.info("Deleting department with id: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department", id));

        departmentRepository.delete(department);

        log.info("Department deleted successfully: {}", department.getName());
    }
}
