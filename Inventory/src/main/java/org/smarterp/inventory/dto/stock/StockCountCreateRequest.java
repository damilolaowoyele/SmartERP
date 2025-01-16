package org.smarterp.inventory.dto.stock;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.smarterp.inventory.entity.StockCount;

import java.util.UUID;

@Data
public class StockCountCreateRequest {
    @NotNull(message = "Item ID is required")
    private UUID itemId;

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    private UUID sectionId;
    private UUID itemBatchId;

    @NotNull(message = "Stock type is required")
    private StockCount.CountType countType;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must not be negative")
    private Integer quantity;
}
