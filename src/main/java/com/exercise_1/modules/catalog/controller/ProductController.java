package com.exercise_1.modules.catalog.controller;

import com.exercise_1.modules.catalog.dto.ProductSearchResponseDto;
import com.exercise_1.modules.catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

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