package com.exercise_1.modules.sale.dto;

import com.exercise_1.modules.order.entity.OrderStatus;
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
public class SaleDetailResponseDto {

    private Boolean success;
    private String message;
    private SaleDetailData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleDetailData {
        private Long id;
        private String number;
        private SaleStatus status;
        private String currency;
        private String notes;
        private Instant createdAt;
        private Instant updatedAt;

        private CustomerInfo customer;
        private WarehouseInfo warehouse;
        private PaymentInfo payment;
        private UserInfo user;

        private List<SaleDetailInfo> details;
        private OrderInfo order;
        private List<AdvanceInfo> advances;
        private TotalsInfo totals;
        private ReservationsInfo reservations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleDetailInfo {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderInfo {
        private Long id;
        private String number;
        private OrderStatus status;
        private String notes;
        private Instant createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdvanceInfo {
        private Long id;
        private BigDecimal amount;
        private Instant createdAt;
        private String username;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalsInfo {
        private BigDecimal saleTotal;
        private BigDecimal totalAdvances;
        private BigDecimal balance;
        private Long itemCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationsInfo {
        private Long count;
        private String status;
    }
}
