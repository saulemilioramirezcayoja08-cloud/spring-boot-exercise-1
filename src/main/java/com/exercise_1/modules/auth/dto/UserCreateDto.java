package com.exercise_1.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotBlank
    @Size(max = 60)
    private String username;

    @Email
    @Size(max = 160)
    private String email;

    @Size(max = 160)
    private String name;

    @NotBlank
    @Size(min = 6, max = 255)
    private String password;

    private List<Long> roleIds;
}