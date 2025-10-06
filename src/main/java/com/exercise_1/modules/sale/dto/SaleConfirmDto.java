package com.exercise_1.modules.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleConfirmDto {

    private String confirmNotes;
    private Long userId;
}