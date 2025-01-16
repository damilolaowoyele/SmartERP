package org.smarterp.inventory.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public static ResourceAlreadyExistsException forField(String resourceName, String field, Object value) {
        return new ResourceAlreadyExistsException(
                String.format("%s already exists with %s: %s", resourceName, field, value));
    }
}
