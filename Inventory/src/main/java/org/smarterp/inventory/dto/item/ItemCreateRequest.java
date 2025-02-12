package org.smarterp.inventory.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
public class ItemCreateRequest {
    private UUID productId;

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 255, message = "Item name must be between 2 and 255 characters")
    private String itemName;

    private String barcode;
    private String description;
    private Set<UUID> categoryIds;

    @NotBlank(message = "Unit of measure is required")
    private String unitOfMeasure;

    @Positive(message = "Weight must be positive")
    private BigDecimal weight;

    @Positive(message = "Volume must be positive")
    private BigDecimal volume;

    @NotNull(message = "Temperature sensitive flag is required")
    private Boolean temperatureSensitive;

    private String storageConditions;
    private String handlingInstructions;
    private String remarks;
}
