package org.smarterp.inventory.dto.item;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
public class ItemUpdateRequest {
    private UUID productId;

    @Size(min = 2, max = 255, message = "Item name must be between 2 and 255 characters")
    private String itemName;

    @Size(max = 50, message = "Barcode must be less than 50 characters")
    private String barcode;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    private Set<UUID> categoryIds;

    @Size(max = 50, message = "Unit of measure must be less than 50 characters")
    private String unitOfMeasure;

    @Positive(message = "Weight must be positive")
    private BigDecimal weight;

    @Positive(message = "Volume must be positive")
    private BigDecimal volume;

    private Boolean temperatureSensitive;
    private String storageConditions;
    private String handlingInstructions;
    private String remarks;
}
