package com.exercise_1.modules.purchase.dto;

import com.exercise_1.modules.purchase.entity.PurchaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponseDto {

    private Boolean success;
    private String message;
    private PurchaseData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseData {
        private Long id;
        private String number;
        private PurchaseStatus status;
    }
}