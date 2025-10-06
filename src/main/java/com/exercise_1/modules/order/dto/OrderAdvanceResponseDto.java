package com.exercise_1.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAdvanceResponseDto {

    private Boolean success;
    private String message;
    private OrderAdvanceData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderAdvanceData {
        private Long id;
        private Long orderId;
        private BigDecimal amount;
    }
}