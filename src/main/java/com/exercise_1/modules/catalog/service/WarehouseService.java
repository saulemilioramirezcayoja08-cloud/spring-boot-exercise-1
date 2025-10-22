package com.exercise_1.modules.catalog.service;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.catalog.dto.WarehouseCreateDto;
import com.exercise_1.modules.catalog.dto.WarehouseResponseDto;
import com.exercise_1.modules.catalog.entity.Warehouse;
import com.exercise_1.modules.catalog.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    @Transactional
    public WarehouseResponseDto create(WarehouseCreateDto dto) {

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return WarehouseResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        if (warehouseRepository.existsByCode(dto.getCode())) {
            return WarehouseResponseDto.builder()
                    .success(false)
                    .message("Warehouse with code " + dto.getCode() + " already exists")
                    .data(null)
                    .build();
        }

        try {
            Warehouse warehouse = Warehouse.builder()
                    .code(dto.getCode())
                    .name(dto.getName())
                    .address(dto.getAddress())
                    .active(true)
                    .user(dto.getUserId() != null ?
                            User.builder().id(dto.getUserId()).build() : null)
                    .build();

            Warehouse savedWarehouse = warehouseRepository.save(warehouse);

            return WarehouseResponseDto.builder()
                    .success(true)
                    .message("Warehouse created successfully")
                    .data(WarehouseResponseDto.WarehouseData.builder()
                            .id(savedWarehouse.getId())
                            .code(savedWarehouse.getCode())
                            .name(savedWarehouse.getName())
                            .address(savedWarehouse.getAddress())
                            .build())
                    .build();

        } catch (Exception e) {
            return WarehouseResponseDto.builder()
                    .success(false)
                    .message("Error creating warehouse: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}