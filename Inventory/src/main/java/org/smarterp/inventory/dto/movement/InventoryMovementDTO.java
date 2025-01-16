package org.smarterp.inventory.dto.movement;


import lombok.Data;
import org.smarterp.inventory.entity.InventoryMovement;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InventoryMovementDTO {
    private UUID movementId;
    private UUID itemBatchId;
    private UUID sourceWarehouseId;
    private UUID destinationWarehouseId;
    private UUID sourceWarehouseSectionId;
    private UUID destinationWarehouseSectionId;
    private InventoryMovement.MovementType movementType;
    private Integer quantity;
    private LocalDateTime movementDate;
    private String remarks;
    private UUID createdBy;
}
