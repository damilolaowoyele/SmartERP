package org.smarterp.inventory.exception;

public class InventoryValidationException extends RuntimeException {
    public InventoryValidationException(String message) {
        super(message);
    }

    public static InventoryValidationException invalidQuantity(String message) {
        return new InventoryValidationException(
                String.format("Invalid quantity: %s", message));
    }

    public static InventoryValidationException invalidDate(String message) {
        return new InventoryValidationException(
                String.format("Invalid date: %s", message));
    }
}