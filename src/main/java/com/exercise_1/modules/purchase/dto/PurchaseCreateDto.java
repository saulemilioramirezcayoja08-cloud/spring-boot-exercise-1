package com.exercise_1.modules.purchase.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
public class PurchaseCreateDto {

    @NotNull
    private Long supplierId;

    @NotNull
    private Long warehouseId;

    @Size(max = 3)
    private String currency;

    private Long paymentId;

    private String notes;

    private Long userId;

    @NotEmpty
    @Valid
    private List<PurchaseDetailDto> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseDetailDto {

        @NotNull
        private Long productId;

        @NotNull
        @Min(value = 1)
        private Integer quantity;

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal price;

        private String notes;
    }
}