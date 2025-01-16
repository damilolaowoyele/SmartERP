package org.smarterp.inventory.dto.section;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class WarehouseSectionDTO {
    private UUID sectionId;
    private String name;
    private UUID warehouseId;
    private BigDecimal capacity;
    private Set<UUID> stockCountIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

