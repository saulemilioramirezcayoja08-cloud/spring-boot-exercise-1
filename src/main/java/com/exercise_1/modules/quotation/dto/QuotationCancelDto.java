package com.exercise_1.modules.quotation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationCancelDto {

    private String cancelNotes;
    private Long userId;
}