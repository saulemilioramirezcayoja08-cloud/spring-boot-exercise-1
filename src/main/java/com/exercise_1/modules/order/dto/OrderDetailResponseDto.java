package com.exercise_1.modules.order.dto;

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
public class OrderDetailResponseDto {

    private Boolean success;
    private String message;
    private OrderDetailData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailData {
        private Long id;
        private String number;
        private OrderStatus status;
        private String currency;
        private String notes;
        private Instant createdAt;
        private Instant updatedAt;

        private CustomerInfo customer;
        private WarehouseInfo warehouse;
        private PaymentInfo payment;
        private UserInfo user;
        private QuotationInfo quotation;

        private List<OrderDetailInfo> details;
        private List<AdvanceInfo> advances;
        private TotalsInfo totals;
        private SaleInfo sale;
        private ReservationsInfo reservations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private Long id;
        private String name;
        private String address;
        private String phone;
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
    public static class QuotationInfo {
        private Long id;
        private String number;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailInfo {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private String uom;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
        private String notes;
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
        private BigDecimal orderTotal;
        private BigDecimal totalAdvances;
        private BigDecimal pendingAmount;
        private Long itemCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleInfo {
        private Long id;
        private String number;
        private SaleStatus status;
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