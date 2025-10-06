package com.exercise_1.modules.quotation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationConfirmDto {

    private Long paymentId;
    private String confirmNotes;
    private Long userId;
}