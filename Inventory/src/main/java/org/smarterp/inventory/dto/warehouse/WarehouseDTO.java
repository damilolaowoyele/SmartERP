package org.smarterp.inventory.dto.warehouse;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class WarehouseDTO {
    private UUID warehouseId;
    private String name;
    private String location;
    private BigDecimal capacity;
    private Set<UUID> sectionIds;
    private Set<UUID> stockCountIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}