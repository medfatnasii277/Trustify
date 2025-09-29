package com.trustify.policy_service.exception;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new resource not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new resource not found exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new resource not found exception for a specific resource type and identifier.
     *
     * @param resourceName the name of the resource
     * @param fieldName the name of the identifier field
     * @param fieldValue the value of the identifier
     * @return the exception
     */
    public static ResourceNotFoundException of(String resourceName, String fieldName, Object fieldValue) {
        return new ResourceNotFoundException(resourceName + " not found with " + fieldName + ": " + fieldValue);
    }
}