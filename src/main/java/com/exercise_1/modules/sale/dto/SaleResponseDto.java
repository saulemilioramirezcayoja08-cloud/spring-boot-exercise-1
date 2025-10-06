package com.exercise_1.modules.sale.dto;

import com.exercise_1.modules.sale.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseDto {

    private Boolean success;
    private String message;
    private SaleData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleData {
        private Long id;
        private String number;
        private SaleStatus status;
    }
}