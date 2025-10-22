package com.exercise_1.modules.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseCreateDto {

    @NotBlank
    @Size(max = 32)
    private String code;

    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 240)
    private String address;

    private Long userId;
}