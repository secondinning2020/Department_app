package com.enterprise.department;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Department Management System
 * 
 * @author Enterprise Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class DepartmentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(DepartmentManagementApplication.class, args);
    }
}
