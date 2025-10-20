package com.exercise_1.modules.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDetailResponseDto {

    private Boolean success;
    private String message;
    private SupplierDetailData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplierDetailData {
        private Long id;
        private String taxId;
        private String name;
        private String phone;
        private String email;
        private String address;
        private Boolean active;
        private Instant createdAt;
        private Instant updatedAt;
        private Long userId;
    }
}