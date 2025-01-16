package org.smarterp.inventory.dto.section;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WarehouseSectionCreateRequest {
    @NotBlank(message = "Section name is required")
    @Size(min = 2, max = 255, message = "Section name must be between 2 and 255 characters")
    private String name;

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    @Positive(message = "Capacity must be positive")
    private BigDecimal capacity;
}
