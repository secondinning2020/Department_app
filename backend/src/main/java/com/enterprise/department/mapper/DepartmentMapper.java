package com.enterprise.department.mapper;

import com.enterprise.department.dto.DepartmentDTO;
import com.enterprise.department.entity.Department;
import org.springframework.stereotype.Component;

/**
 * Mapper for Department entity and DTO conversion
 */
@Component
public class DepartmentMapper {

    /**
     * Convert Department entity to DTO
     *
     * @param department Department entity
     * @return DepartmentDTO
     */
    public DepartmentDTO toDTO(Department department) {
        if (department == null) {
            return null;
        }

        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setLocation(department.getLocation());
        dto.setEmployeeCount(department.getEmployees() != null ? department.getEmployees().size() : 0);
        dto.setCreatedAt(department.getCreatedAt());
        dto.setUpdatedAt(department.getUpdatedAt());

        return dto;
    }

    /**
     * Convert DepartmentDTO to entity
     *
     * @param dto DepartmentDTO
     * @return Department entity
     */
    public Department toEntity(DepartmentDTO dto) {
        if (dto == null) {
            return null;
        }

        Department department = new Department();
        department.setId(dto.getId());
        department.setName(dto.getName());
        department.setLocation(dto.getLocation());

        return department;
    }

    /**
     * Update existing department entity from DTO
     *
     * @param dto        DepartmentDTO
     * @param department Existing department entity
     */
    public void updateEntityFromDTO(DepartmentDTO dto, Department department) {
        if (dto == null || department == null) {
            return;
        }

        department.setName(dto.getName());
        department.setLocation(dto.getLocation());
    }
}
