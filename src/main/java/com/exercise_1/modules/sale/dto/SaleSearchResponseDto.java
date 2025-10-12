package com.exercise_1.modules.sale.dto;

import com.exercise_1.modules.sale.entity.SaleStatus;
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
public class SaleSearchResponseDto {

    private Boolean success;
    private String message;
    private List<SaleData> data;
    private PaginationMetadata pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleData {
        private Long id;
        private String number;
        private SaleStatus status;
        private String customerName;
        private String warehouseName;
        private String username;
        private String currency;
        private BigDecimal saleTotalAmount;
        private Long itemCount;
        private String paymentName;
        private Long orderId;
        private BigDecimal orderTotalAdvances;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationMetadata {
        private Integer currentPage;
        private Integer totalPages;
        private Long totalElements;
        private Integer pageSize;
        private Boolean hasNext;
        private Boolean hasPrevious;
    }
}