package com.exercise_1.modules.order.dto;

import com.exercise_1.modules.order.entity.OrderStatus;
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
public class OrderSearchResponseDto {

    private Boolean success;
    private String message;
    private List<OrderData> data;
    private PaginationMetadata pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderData {
        private Long id;
        private String number;
        private OrderStatus status;
        private String customerName;
        private String warehouseName;
        private String username;
        private String currency;
        private BigDecimal totalAmount;
        private Long itemCount;
        private String paymentName;
        private BigDecimal totalAdvances;
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