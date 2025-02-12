package org.smarterp.inventory.dto.item;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class ItemDTO {
    private UUID itemId;
    private UUID productId;
    private String itemName;
    private String barcode;
    private String description;
    private Set<UUID> categoryIds;
    private String unitOfMeasure;
    private BigDecimal weight;
    private BigDecimal volume;
    private Boolean temperatureSensitive;
    private String storageConditions;
    private String handlingInstructions;
    private String remarks;
    private Set<UUID> itemBatchIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
