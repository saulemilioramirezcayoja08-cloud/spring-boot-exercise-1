package com.exercise_1.modules.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponseDto {

    private Boolean success;
    private String message;
    private WarehouseData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseData {
        private Long id;
        private String code;
        private String name;
        private String address;
    }
}