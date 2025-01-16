package org.smarterp.inventory.dto.category;

import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.UUID;

@Data
public class CategoryUpdateRequest {
    @Size(min = 2, max = 255, message = "Category name must be between 2 and 255 characters")
    private String name;

    private String description;
    private UUID parentCategoryId;
}
