package com.enterprise.department.exception;

/**
 * Exception thrown when an entity is not found
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, String id) {
        super(String.format("%s not found with id: %s", entityName, id));
    }

    public EntityNotFoundException(String entityName, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s: %s", entityName, fieldName, fieldValue));
    }
}
