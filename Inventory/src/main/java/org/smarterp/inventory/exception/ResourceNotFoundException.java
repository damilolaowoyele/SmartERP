package org.smarterp.inventory.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException forId(String resourceName, Object id) {
        return new ResourceNotFoundException(
                String.format("%s not found with id: %s", resourceName, id));
    }

    public static ResourceNotFoundException forField(String resourceName, String field, Object value) {
        return new ResourceNotFoundException(
                String.format("%s not found with %s: %s", resourceName, field, value));
    }
}
