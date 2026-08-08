package org.smarterp.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.Repository.ProductRepository;
import org.smarterp.inventory.dto.product.ProductCreateRequest;
import org.smarterp.inventory.dto.product.ProductDTO;
import org.smarterp.inventory.dto.product.ProductUpdateRequest;
import org.smarterp.inventory.entity.Product;
import org.smarterp.inventory.exception.ResourceAlreadyExistsException;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.ProductMapper;
import org.smarterp.inventory.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO createProduct(ProductCreateRequest request) {
        if (productRepository.existsByProductNameIgnoreCase(request.getProductName())) {
            throw ResourceAlreadyExistsException.forField("Product", "name", request.getProductName());
        }

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(UUID productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", productId));

        if (request.getProductName() != null && !request.getProductName().equals(product.getProductName())) {
            if (productRepository.existsByProductNameIgnoreCase(request.getProductName())) {
                throw ResourceAlreadyExistsException.forField("Product", "name", request.getProductName());
            }
            product.setProductName(request.getProductName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getDefaultCost() != null) {
            product.setDefaultCost(request.getDefaultCost());
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", productId));
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProductsByName(String name) {
        return productRepository.findByProductNameContainingIgnoreCase(name).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByPriceRange(BigDecimal maxPrice) {
        return productRepository.findByPriceLessThanEqual(maxPrice).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw ResourceNotFoundException.forField("ItemBatch", "batchId", productId);
        }
        productRepository.deleteById(productId);
    }
}
