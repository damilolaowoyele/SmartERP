package org.smarterp.inventory.dto.category;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class CategoryDTO {
    private UUID categoryId;
    private String name;
    private String description;
    private UUID parentCategoryId;
    private Set<UUID> childCategoryIds;
    private Set<UUID> itemIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


