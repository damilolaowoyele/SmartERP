package org.smarterp.inventory.dto.product;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class ProductDTO {
    private UUID productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private BigDecimal defaultCost;
    private Set<UUID> itemIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
