package com.exercise_1.modules.purchase.dto;

import com.exercise_1.modules.purchase.entity.PurchaseStatus;
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
public class PurchaseSearchResponseDto {

    private Boolean success;
    private String message;
    private List<PurchaseData> data;
    private PaginationMetadata pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseData {
        private Long id;
        private String number;
        private PurchaseStatus status;
        private String supplierName;
        private String warehouseName;
        private String username;
        private String currency;
        private BigDecimal totalAmount;
        private Long itemCount;
        private String paymentName;
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