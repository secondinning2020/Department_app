package com.enterprise.department.util;

import com.enterprise.department.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Employee ID Generator
 * Generates sequential employee IDs in format: EMP001, EMP002, etc.
 * Thread-safe implementation using AtomicInteger
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeIdGenerator {

    private final EmployeeRepository employeeRepository;
    private final AtomicInteger counter = new AtomicInteger(0);
    private volatile boolean initialized = false;

    /**
     * Generate next employee ID
     * Format: EMP001, EMP002, etc.
     *
     * @return Next employee ID
     */
    public synchronized String generateNextId() {
        if (!initialized) {
            initializeCounter();
        }

        int nextNumber = counter.incrementAndGet();
        String employeeId = formatEmployeeId(nextNumber);

        log.debug("Generated employee ID: {}", employeeId);
        return employeeId;
    }

    /**
     * Initialize counter from database
     * Finds the maximum existing employee ID and sets counter accordingly
     */
    private void initializeCounter() {
        try {
            String maxId = employeeRepository.findMaxEmployeeId();

            if (maxId != null && maxId.startsWith("EMP")) {
                // Extract numeric part from EMP001 -> 1
                String numericPart = maxId.substring(3);
                int maxNumber = Integer.parseInt(numericPart);
                counter.set(maxNumber);
                log.info("Initialized employee ID counter from database. Starting from: {}", maxNumber);
            } else {
                counter.set(0);
                log.info("No existing employees found. Starting counter from 0");
            }

            initialized = true;
        } catch (Exception e) {
            log.error("Error initializing employee ID counter. Starting from 0", e);
            counter.set(0);
            initialized = true;
        }
    }

    /**
     * Format employee ID with leading zeros
     * Example: 1 -> EMP001, 25 -> EMP025, 1000 -> EMP1000
     *
     * @param number Sequence number
     * @return Formatted employee ID
     */
    private String formatEmployeeId(int number) {
        return String.format("EMP%03d", number);
    }

    /**
     * Reset counter (for testing purposes)
     */
    public synchronized void resetCounter() {
        counter.set(0);
        initialized = false;
        log.warn("Employee ID counter has been reset");
    }
}
