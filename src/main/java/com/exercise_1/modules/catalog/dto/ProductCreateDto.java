package com.exercise_1.modules.catalog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {

    @NotBlank
    @Size(max = 80)
    private String sku;

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 5000)
    private String description;

    private Long categoryId;

    @NotBlank
    @Size(max = 16)
    private String uom;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    private Long userId;

    @Valid
    private List<CodeDto> codes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeDto {

        @NotBlank
        @Size(max = 80)
        private String type;

        @NotBlank
        @Size(max = 120)
        private String code;
    }
}