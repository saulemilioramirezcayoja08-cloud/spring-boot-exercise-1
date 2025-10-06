package com.exercise_1.modules.auth.service;

import com.exercise_1.modules.auth.dto.LoginRequestDto;
import com.exercise_1.modules.auth.dto.LoginResponseDto;
import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {

        if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return LoginResponseDto.builder()
                    .success(false)
                    .message("Username and password are required")
                    .data(null)
                    .build();
        }

        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElse(null);

            if (user == null || !user.getPassword().equals(request.getPassword())) {
                return LoginResponseDto.builder()
                        .success(false)
                        .message("Invalid username or password")
                        .data(null)
                        .build();
            }

            if (!user.isActive()) {
                return LoginResponseDto.builder()
                        .success(false)
                        .message("User account is inactive")
                        .data(null)
                        .build();
            }

            List<String> roles = userRepository.findRoleCodesByUserId(user.getId());

            LoginResponseDto.LoginData loginData = LoginResponseDto.LoginData.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .name(user.getName())
                    .roles(roles)
                    .build();

            return LoginResponseDto.builder()
                    .success(true)
                    .message("Login successful")
                    .data(loginData)
                    .build();

        } catch (Exception e) {
            return LoginResponseDto.builder()
                    .success(false)
                    .message("Error during login: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}