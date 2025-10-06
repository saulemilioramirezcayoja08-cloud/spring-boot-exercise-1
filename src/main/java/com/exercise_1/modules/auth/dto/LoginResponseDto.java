package com.exercise_1.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private Boolean success;
    private String message;
    private LoginData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginData {
        private Long userId;
        private String username;
        private String email;
        private String name;
        private List<String> roles;
    }
}