package org.smarterp.inventory.mapper;


import org.smarterp.inventory.dto.product.ProductCreateRequest;
import org.smarterp.inventory.dto.product.ProductDTO;
import org.smarterp.inventory.entity.Product;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        if (product == null) return null;

        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDefaultCost(product.getDefaultCost());

        dto.setItemIds(product.getItems().stream()
                .map(item -> item.getItemId())
                .collect(Collectors.toSet()));

        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        return dto;
    }

    public Product toEntity(ProductCreateRequest request) {
        if (request == null) return null;

        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDefaultCost(request.getDefaultCost());

        return product;
    }
}
