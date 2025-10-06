package com.exercise_1.modules.sale.dto;

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
public class SaleHistoryResponseDto {

    private Boolean success;
    private String message;
    private List<SaleHistoryData> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleHistoryData {
        private String number;
        private Integer quantity;
        private BigDecimal price;
    }
}