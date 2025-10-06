package com.exercise_1.modules.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponseDto {

    private Boolean success;
    private String message;
    private List<ProductData> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductData {
        private Long id;
        private String sku;
        private String name;
        private String uom;
        private BigDecimal price;
    }
}