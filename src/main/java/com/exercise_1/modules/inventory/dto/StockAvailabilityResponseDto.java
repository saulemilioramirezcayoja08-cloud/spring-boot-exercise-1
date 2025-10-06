package com.exercise_1.modules.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAvailabilityResponseDto {

    private Boolean success;
    private String message;
    private List<WarehouseStock> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseStock {
        private String code;
        private Integer existence;
        private Integer reserved;
        private Integer available;
    }
}