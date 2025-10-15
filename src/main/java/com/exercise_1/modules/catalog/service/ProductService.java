package com.exercise_1.modules.catalog.service;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.catalog.dto.ProductCreateDto;
import com.exercise_1.modules.catalog.dto.ProductResponseDto;
import com.exercise_1.modules.catalog.dto.ProductSearchResponseDto;
import com.exercise_1.modules.catalog.entity.Category;
import com.exercise_1.modules.catalog.entity.Code;
import com.exercise_1.modules.catalog.entity.Product;
import com.exercise_1.modules.catalog.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProductResponseDto create(ProductCreateDto dto) {

        if (dto.getCategoryId() != null && !categoryRepository.existsById(dto.getCategoryId())) {
            return ProductResponseDto.builder()
                    .success(false)
                    .message("Category not found")
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return ProductResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        if (productRepository.existsBySku(dto.getSku())) {
            return ProductResponseDto.builder()
                    .success(false)
                    .message("Product with SKU " + dto.getSku() + " already exists")
                    .data(null)
                    .build();
        }

        try {
            Product product = Product.builder()
                    .sku(dto.getSku())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .category(dto.getCategoryId() != null ?
                            Category.builder().id(dto.getCategoryId()).build() : null)
                    .uom(dto.getUom())
                    .price(dto.getPrice())
                    .active(true)
                    .user(dto.getUserId() != null ?
                            User.builder().id(dto.getUserId()).build() : null)
                    .build();

            if (dto.getCodes() != null && !dto.getCodes().isEmpty()) {
                List<Code> codes = dto.getCodes().stream()
                        .map(codeDto -> Code.builder()
                                .product(product)
                                .type(codeDto.getType())
                                .code(codeDto.getCode())
                                .user(dto.getUserId() != null ?
                                        User.builder().id(dto.getUserId()).build() : null)
                                .build())
                        .collect(Collectors.toList());

                product.setCodes(codes);
            }

            Product savedProduct = productRepository.save(product);

            return ProductResponseDto.builder()
                    .success(true)
                    .message("Product created successfully")
                    .data(ProductResponseDto.ProductData.builder()
                            .id(savedProduct.getId())
                            .sku(savedProduct.getSku())
                            .name(savedProduct.getName())
                            .build())
                    .build();

        } catch (Exception e) {
            return ProductResponseDto.builder()
                    .success(false)
                    .message("Error creating product: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

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