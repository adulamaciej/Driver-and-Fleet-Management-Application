package org.example.driverandfleetmanagementapp.exception.custom;


public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
