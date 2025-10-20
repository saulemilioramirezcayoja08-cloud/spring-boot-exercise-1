package com.exercise_1.modules.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {

    private Boolean success;
    private String message;
    private CustomerData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerData {
        private Long id;
        private String taxId;
        private String name;
        private String email;
    }
}