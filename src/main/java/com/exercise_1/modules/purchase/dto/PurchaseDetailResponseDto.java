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
public class PurchaseDetailResponseDto {

    private Boolean success;
    private String message;
    private PurchaseDetailData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseDetailData {
        private Long id;
        private String number;
        private PurchaseStatus status;
        private String currency;
        private String notes;
        private Instant createdAt;
        private Instant updatedAt;

        private SupplierInfo supplier;
        private WarehouseInfo warehouse;
        private PaymentInfo payment;
        private UserInfo user;

        private List<PurchaseDetailInfo> details;
        private TotalsInfo totals;
        private StockInfo stockInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplierInfo {
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
    public static class PurchaseDetailInfo {
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
    public static class TotalsInfo {
        private BigDecimal purchaseTotal;
        private Long itemCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockInfo {
        private Boolean stockIncreased;
    }
}