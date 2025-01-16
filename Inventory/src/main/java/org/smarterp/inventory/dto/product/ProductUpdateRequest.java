package org.smarterp.inventory.dto.product;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    private String productName;

    private String description;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Positive(message = "Default cost must be positive")
    private BigDecimal defaultCost;
}
