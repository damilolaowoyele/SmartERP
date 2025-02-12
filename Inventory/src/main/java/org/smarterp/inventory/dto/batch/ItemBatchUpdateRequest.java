package org.smarterp.inventory.dto.batch;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ItemBatchUpdateRequest {
    private String batchNumber;

    private LocalDate manufactureDate;

    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    private UUID warehouseId;
    private UUID sectionId;
}