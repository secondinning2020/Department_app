package com.enterprise.department.service;

import com.enterprise.department.entity.Department;
import com.enterprise.department.entity.Employee;
import com.enterprise.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Report Service
 * Handles PDF report generation using JasperReports
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final DepartmentRepository departmentRepository;

    /**
     * Generate department report PDF
     * One department per page with employee list and salary totals
     *
     * @return PDF byte array
     * @throws Exception if report generation fails
     */
    public byte[] generateDepartmentReport() throws Exception {
        log.info("Generating department report");

        try {
            // Load JRXML template
            InputStream reportStream = new ClassPathResource("reports/department_report.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Fetch all departments with employees
            List<Department> departments = departmentRepository.findAll();

            log.info("Generating report for {} departments", departments.size());

            // Prepare data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(departments);

            // Parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportTitle", "Department and Employee Report");
            parameters.put("GeneratedBy", "Department Management System");

            // Fill report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export to PDF
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            log.info("Department report generated successfully, size: {} bytes", pdfBytes.length);

            return pdfBytes;

        } catch (Exception e) {
            log.error("Error generating department report", e);
            throw new RuntimeException("Failed to generate department report: " + e.getMessage(), e);
        }
    }

    /**
     * Calculate total salary for a department
     *
     * @param department Department
     * @return Total salary
     */
    public Double calculateDepartmentTotalSalary(Department department) {
        return department.getEmployees().stream()
                .mapToDouble(Employee::getSalary)
                .sum();
    }
}
