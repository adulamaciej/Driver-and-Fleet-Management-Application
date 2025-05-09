package org.example.driverandfleetmanagementapp.exception.custom;


public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
