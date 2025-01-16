package org.smarterp.inventory.exception;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }

    public static InvalidOperationException insufficientStock(String itemName, String location) {
        return new InvalidOperationException(
                String.format("Insufficient stock for item '%s' at location '%s'", itemName, location));
    }

    public static InvalidOperationException invalidMovement(String reason) {
        return new InvalidOperationException(
                String.format("Invalid inventory movement: %s", reason));
    }
}
