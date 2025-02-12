package org.smarterp.inventory.dto.stock;

import lombok.Data;
import org.smarterp.inventory.entity.StockCount;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class StockCountDTO {
    private UUID countId;
    private UUID itemId;
    private UUID warehouseId;
    private UUID sectionId;
    private UUID itemBatchId;
    private StockCount.CountType countType;
    private Integer quantity;
    private LocalDateTime lastUpdated;
}
