package org.smarterp.inventory.dto.warehouse;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WarehouseUpdateRequest {
    @Size(min = 2, max = 255, message = "Warehouse name must be between 2 and 255 characters")
    private String name;

    private String location;

    @Positive(message = "Capacity must be positive")
    private BigDecimal capacity;
}
