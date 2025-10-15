package com.exercise_1.modules.catalog.controller;

import com.exercise_1.modules.catalog.dto.ProductCreateDto;
import com.exercise_1.modules.catalog.dto.ProductResponseDto;
import com.exercise_1.modules.catalog.dto.ProductSearchResponseDto;
import com.exercise_1.modules.catalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductCreateDto dto) {
        ProductResponseDto response = productService.create(dto);

        if (response.getSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if (response.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ProductSearchResponseDto> search(
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String name) {

        ProductSearchResponseDto response = productService.search(sku, name);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}