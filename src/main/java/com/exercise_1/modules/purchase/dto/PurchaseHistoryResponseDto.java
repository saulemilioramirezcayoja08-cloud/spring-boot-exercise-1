package com.exercise_1.modules.purchase.dto;

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
public class PurchaseHistoryResponseDto {

    private Boolean success;
    private String message;
    private List<PurchaseHistoryData> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseHistoryData {
        private String number;
        private Integer quantity;
        private BigDecimal price;
    }
}