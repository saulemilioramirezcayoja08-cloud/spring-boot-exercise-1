package com.exercise_1.modules.partner.dto;

import jakarta.validation.constraints.Email;
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
public class SupplierCreateDto {

    @Size(max = 40)
    private String taxId;

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 40)
    private String phone;

    @Email
    @Size(max = 160)
    private String email;

    @Size(max = 240)
    private String address;

    private Long userId;
}