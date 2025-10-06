package com.exercise_1.modules.catalog.service;

import com.exercise_1.modules.catalog.dto.ProductSearchResponseDto;
import com.exercise_1.modules.catalog.entity.Product;
import com.exercise_1.modules.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductSearchResponseDto search(String sku, String name) {

        if (sku == null && name == null) {
            return ProductSearchResponseDto.builder()
                    .success(false)
                    .message("At least one search parameter (sku or name) is required")
                    .data(null)
                    .build();
        }

        try {
            List<Product> products;

            if (sku != null && name != null) {
                products = productRepository.findBySkuContainingIgnoreCaseAndNameContainingIgnoreCase(sku, name);
            } else if (sku != null) {
                products = productRepository.findBySkuContainingIgnoreCase(sku);
            } else {
                products = productRepository.findByNameContainingIgnoreCase(name);
            }

            if (products.isEmpty()) {
                return ProductSearchResponseDto.builder()
                        .success(true)
                        .message("No products found")
                        .data(List.of())
                        .build();
            }

            List<ProductSearchResponseDto.ProductData> productDataList = products.stream()
                    .map(product -> ProductSearchResponseDto.ProductData.builder()
                            .id(product.getId())
                            .sku(product.getSku())
                            .name(product.getName())
                            .uom(product.getUom())
                            .price(product.getPrice())
                            .build())
                    .collect(Collectors.toList());

            return ProductSearchResponseDto.builder()
                    .success(true)
                    .message("Products found successfully")
                    .data(productDataList)
                    .build();

        } catch (Exception e) {
            return ProductSearchResponseDto.builder()
                    .success(false)
                    .message("Error searching products: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}