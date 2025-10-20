package com.exercise_1.modules.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDto {

    private Boolean success;
    private String message;
    private SupplierData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplierData {
        private Long id;
        private String taxId;
        private String name;
        private String email;
    }
}