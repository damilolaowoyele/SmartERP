package org.smarterp.inventory.dto.batch;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class ItemBatchDTO {
    private UUID batchId;
    private UUID itemId;
    private String batchNumber;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private Integer quantity;
    private UUID warehouseId;
    private UUID sectionId;
    private Set<UUID> movementIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
