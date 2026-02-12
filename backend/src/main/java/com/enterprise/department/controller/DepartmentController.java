package com.enterprise.department.controller;

import com.enterprise.department.dto.DepartmentDTO;
import com.enterprise.department.service.DepartmentService;
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

/**
 * Department Controller
 * REST endpoints for department management
 */
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Departments", description = "Department management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Get all departments
     * All authenticated users can access
     */
    @GetMapping
    @Operation(summary = "Get all departments", description = "Retrieve all departments")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        log.info("GET /api/v1/departments - Fetching all departments");

        List<DepartmentDTO> departments = departmentService.getAllDepartments();

        return ResponseEntity.ok(departments);
    }

    /**
     * Get department by ID
     * All authenticated users can access
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Retrieve department by ID")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable String id) {
        log.info("GET /api/v1/departments/{} - Fetching department", id);

        DepartmentDTO department = departmentService.getDepartmentById(id);

        return ResponseEntity.ok(department);
    }

    /**
     * Create new department
     * ADMIN and HR can access
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Create department", description = "Create new department (Admin/HR only)")
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("POST /api/v1/departments - Creating department: {}", departmentDTO.getName());

        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
    }

    /**
     * Update department
     * ADMIN and HR can access
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Update department", description = "Update existing department (Admin/HR only)")
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @PathVariable String id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {

        log.info("PUT /api/v1/departments/{} - Updating department", id);

        DepartmentDTO updatedDepartment = departmentService.updateDepartment(id, departmentDTO);

        return ResponseEntity.ok(updatedDepartment);
    }

    /**
     * Delete department
     * ADMIN only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete department", description = "Delete department (Admin only)")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String id) {
        log.info("DELETE /api/v1/departments/{} - Deleting department", id);

        departmentService.deleteDepartment(id);

        return ResponseEntity.noContent().build();
    }
}
