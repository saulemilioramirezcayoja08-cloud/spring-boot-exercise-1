package com.exercise_1.modules.order.dto;

import com.exercise_1.modules.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private Boolean success;
    private String message;
    private OrderData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderData {
        private Long id;
        private String number;
        private OrderStatus status;
    }
}