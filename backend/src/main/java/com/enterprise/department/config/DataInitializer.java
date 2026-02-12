package com.enterprise.department.config;

import com.enterprise.department.entity.Department;
import com.enterprise.department.entity.Employee;
import com.enterprise.department.entity.User;
import com.enterprise.department.repository.DepartmentRepository;
import com.enterprise.department.repository.EmployeeRepository;
import com.enterprise.department.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Data Initializer
 * Seeds initial data for development
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("Initializing sample data...");

            // Create users if not exist
            if (userRepository.count() == 0) {
                createUsers();
            }

            // Create departments if not exist
            if (departmentRepository.count() == 0) {
                createDepartments();
            }

            log.info("Data initialization completed");
        };
    }

    private void createUsers() {
        // Admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ADMIN);
        admin.setEnabled(true);
        admin.setAccountNonExpired(true);
        admin.setAccountNonLocked(true);
        admin.setCredentialsNonExpired(true);
        userRepository.save(admin);
        log.info("Created admin user: admin/admin123");

        // HR user
        User hr = new User();
        hr.setUsername("hr");
        hr.setPassword(passwordEncoder.encode("hr123"));
        hr.setRole(User.Role.HR);
        hr.setEnabled(true);
        hr.setAccountNonExpired(true);
        hr.setAccountNonLocked(true);
        hr.setCredentialsNonExpired(true);
        userRepository.save(hr);
        log.info("Created HR user: hr/hr123");

        // Employee user
        User employee = new User();
        employee.setUsername("employee");
        employee.setPassword(passwordEncoder.encode("emp123"));
        employee.setRole(User.Role.EMPLOYEE);
        employee.setEnabled(true);
        employee.setAccountNonExpired(true);
        employee.setAccountNonLocked(true);
        employee.setCredentialsNonExpired(true);
        userRepository.save(employee);
        log.info("Created employee user: employee/emp123");
    }

    private void createDepartments() {
        // IT Department
        Department itDept = new Department();
        itDept.setName("Information Technology");
        itDept.setLocation("Building A, Floor 3");
        Department savedItDept = departmentRepository.save(itDept);
        log.info("Created department: {}", itDept.getName());

        // Create employees for IT
        createEmployee("EMP001", "John Doe", "john.doe@enterprise.com", "Software Engineer", 75000.0, savedItDept);
        createEmployee("EMP002", "Jane Smith", "jane.smith@enterprise.com", "Senior Developer", 95000.0, savedItDept);
        createEmployee("EMP003", "Bob Johnson", "bob.johnson@enterprise.com", "DevOps Engineer", 85000.0, savedItDept);

        // HR Department
        Department hrDept = new Department();
        hrDept.setName("Human Resources");
        hrDept.setLocation("Building B, Floor 2");
        Department savedHrDept = departmentRepository.save(hrDept);
        log.info("Created department: {}", hrDept.getName());

        // Create employees for HR
        createEmployee("EMP004", "Alice Williams", "alice.williams@enterprise.com", "HR Manager", 80000.0, savedHrDept);
        createEmployee("EMP005", "Charlie Brown", "charlie.brown@enterprise.com", "Recruiter", 60000.0, savedHrDept);

        // Finance Department
        Department financeDept = new Department();
        financeDept.setName("Finance");
        financeDept.setLocation("Building A, Floor 1");
        Department savedFinanceDept = departmentRepository.save(financeDept);
        log.info("Created department: {}", financeDept.getName());

        // Create employees for Finance
        createEmployee("EMP006", "David Miller", "david.miller@enterprise.com", "Financial Analyst", 70000.0,
                savedFinanceDept);
        createEmployee("EMP007", "Emma Davis", "emma.davis@enterprise.com", "Accountant", 65000.0, savedFinanceDept);
        createEmployee("EMP008", "Frank Wilson", "frank.wilson@enterprise.com", "CFO", 120000.0, savedFinanceDept);
    }

    private void createEmployee(String id, String name, String email, String position, Double salary,
            Department department) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setPosition(position);
        employee.setSalary(salary);
        employee.setDepartment(department);
        employeeRepository.save(employee);
        log.info("Created employee: {} ({})", name, id);
    }
}
