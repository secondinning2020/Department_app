package com.enterprise.department.controller;

import com.enterprise.department.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Report Controller
 * REST endpoints for report generation
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports", description = "Report generation endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportController {

    private final ReportService reportService;

    /**
     * Generate department report PDF
     * ADMIN and HR can access
     */
    @GetMapping("/departments")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Generate department report", description = "Generate PDF report with all departments and employees (Admin/HR only)")
    public ResponseEntity<byte[]> generateDepartmentReport() {
        log.info("GET /api/v1/reports/departments - Generating department report");

        try {
            byte[] pdfBytes = reportService.generateDepartmentReport();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "department_report.pdf");
            headers.setContentLength(pdfBytes.length);

            log.info("Department report generated successfully");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("Error generating department report", e);
            throw new RuntimeException("Failed to generate report: " + e.getMessage());
        }
    }
}
