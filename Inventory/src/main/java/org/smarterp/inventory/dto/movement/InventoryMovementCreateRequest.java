package org.smarterp.inventory.dto.movement;

import org.smarterp.inventory.entity.InventoryMovement.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.UUID;

@Data
public class InventoryMovementCreateRequest {
    private UUID itemBatchId;

    private UUID sourceWarehouseId;
    private UUID destinationWarehouseId;
    private UUID sourceWarehouseSectionId;
    private UUID destinationWarehouseSectionId;

    @NotNull(message = "Movement type is required")
    private MovementType movementType;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    private String remarks;
}
