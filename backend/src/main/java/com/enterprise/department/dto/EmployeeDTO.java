package com.enterprise.department.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Employee DTO for Request and Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private String id; // EMP001, EMP002, etc.

    @NotBlank(message = "Employee name is required")
    @Size(min = 2, max = 100, message = "Employee name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Position is required")
    @Size(min = 2, max = 100, message = "Position must be between 2 and 100 characters")
    private String position;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be positive")
    @DecimalMin(value = "0.01", message = "Salary must be greater than 0")
    private Double salary;

    private String departmentId;

    private String departmentName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructor without department details
    public EmployeeDTO(String id, String name, String email, String position, Double salary) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.position = position;
        this.salary = salary;
    }
}
