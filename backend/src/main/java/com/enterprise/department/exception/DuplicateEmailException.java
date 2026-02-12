package com.enterprise.department.exception;

/**
 * Exception thrown when attempting to create an employee with a duplicate email
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super(String.format("Employee with email '%s' already exists", email));
    }
}
