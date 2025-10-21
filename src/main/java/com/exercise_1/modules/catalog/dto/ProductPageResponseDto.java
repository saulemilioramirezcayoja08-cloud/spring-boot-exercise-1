package com.exercise_1.modules.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageResponseDto {
    private Boolean success;
    private String message;
    private PageData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageData {
        private List<ProductListItem> content;
        private PaginationInfo pagination;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductListItem {
        private Long id;
        private String sku;
        private String name;
        private String description;
        private String uom;
        private BigDecimal price;
        private Boolean active;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private Integer currentPage;
        private Integer pageSize;
        private Long totalElements;
        private Integer totalPages;
        private Boolean isFirst;
        private Boolean isLast;
        private Boolean hasNext;
        private Boolean hasPrevious;
    }
}