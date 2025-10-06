package com.exercise_1.modules.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleCancelDto {

    private String cancelNotes;
    private Long userId;
}