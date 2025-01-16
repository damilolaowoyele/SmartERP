package org.smarterp.inventory.dto.section;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WarehouseSectionUpdateRequest {
    @Size(min = 2, max = 255, message = "Section name must be between 2 and 255 characters")
    private String name;

    @Positive(message = "Capacity must be positive")
    private BigDecimal capacity;
}
