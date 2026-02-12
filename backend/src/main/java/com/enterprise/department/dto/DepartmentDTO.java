package com.enterprise.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Department DTO for Request and Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

    private String id;

    @NotBlank(message = "Department name is required")
    @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 200, message = "Location must be between 2 and 200 characters")
    private String location;

    private Integer employeeCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructor without employees list for basic responses
    public DepartmentDTO(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }
}
