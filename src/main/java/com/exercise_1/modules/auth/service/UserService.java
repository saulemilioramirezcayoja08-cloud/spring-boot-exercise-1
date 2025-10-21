package com.exercise_1.modules.auth.service;

import com.exercise_1.modules.auth.dto.UserCreateDto;
import com.exercise_1.modules.auth.dto.UserResponseDto;
import com.exercise_1.modules.auth.entity.Role;
import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.entity.UserRole;
import com.exercise_1.modules.auth.entity.UserRoleId;
import com.exercise_1.modules.auth.repository.RoleRepository;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.auth.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    public UserResponseDto create(UserCreateDto dto) {

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return UserResponseDto.builder()
                    .success(false)
                    .message("Username already exists")
                    .data(null)
                    .build();
        }

        if (dto.getEmail() != null && userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return UserResponseDto.builder()
                    .success(false)
                    .message("Email already exists")
                    .data(null)
                    .build();
        }

        try {
            User user = User.builder()
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .name(dto.getName())
                    .password(dto.getPassword())
                    .active(true)
                    .build();

            User savedUser = userRepository.save(user);

            if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
                List<UserRole> userRoles = new ArrayList<>();
                for (Long roleId : dto.getRoleIds()) {
                    Role role = roleRepository.findById(roleId).orElse(null);
                    if (role != null) {
                        UserRole userRole = new UserRole();
                        userRole.setId(new UserRoleId(savedUser.getId(), roleId));
                        userRole.setUser(savedUser);
                        userRole.setRole(role);
                        userRoles.add(userRole);
                    }
                }
                userRoleRepository.saveAll(userRoles);
            }

            return UserResponseDto.builder()
                    .success(true)
                    .message("User created successfully")
                    .data(UserResponseDto.UserData.builder()
                            .id(savedUser.getId())
                            .username(savedUser.getUsername())
                            .email(savedUser.getEmail())
                            .name(savedUser.getName())
                            .build())
                    .build();

        } catch (Exception e) {
            return UserResponseDto.builder()
                    .success(false)
                    .message("Error creating user: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}