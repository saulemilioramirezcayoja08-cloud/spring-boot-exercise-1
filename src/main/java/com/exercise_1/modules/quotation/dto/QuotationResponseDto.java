package com.exercise_1.modules.quotation.dto;

import com.exercise_1.modules.quotation.entity.QuotationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationResponseDto {

    private Boolean success;
    private String message;
    private QuotationData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotationData {
        private Long id;
        private String number;
        private QuotationStatus status;
    }
}