package com.exercise_1.modules.partner.service;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.partner.dto.SupplierCreateDto;
import com.exercise_1.modules.partner.dto.SupplierDetailResponseDto;
import com.exercise_1.modules.partner.dto.SupplierResponseDto;
import com.exercise_1.modules.partner.entity.Supplier;
import com.exercise_1.modules.partner.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    @Transactional
    public SupplierResponseDto create(SupplierCreateDto dto) {

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return SupplierResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        if (dto.getEmail() != null && supplierRepository.existsByEmail(dto.getEmail())) {
            return SupplierResponseDto.builder()
                    .success(false)
                    .message("Supplier with email " + dto.getEmail() + " already exists")
                    .data(null)
                    .build();
        }

        try {
            Supplier supplier = Supplier.builder()
                    .taxId(dto.getTaxId())
                    .name(dto.getName())
                    .phone(dto.getPhone())
                    .email(dto.getEmail())
                    .address(dto.getAddress())
                    .active(true)
                    .user(dto.getUserId() != null ?
                            User.builder().id(dto.getUserId()).build() : null)
                    .build();

            Supplier savedSupplier = supplierRepository.save(supplier);

            return SupplierResponseDto.builder()
                    .success(true)
                    .message("Supplier created successfully")
                    .data(SupplierResponseDto.SupplierData.builder()
                            .id(savedSupplier.getId())
                            .taxId(savedSupplier.getTaxId())
                            .name(savedSupplier.getName())
                            .email(savedSupplier.getEmail())
                            .build())
                    .build();

        } catch (Exception e) {
            return SupplierResponseDto.builder()
                    .success(false)
                    .message("Error creating supplier: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public SupplierDetailResponseDto findById(Long id) {
        return supplierRepository.findById(id)
                .map(supplier -> SupplierDetailResponseDto.builder()
                        .success(true)
                        .message("Supplier found successfully")
                        .data(SupplierDetailResponseDto.SupplierDetailData.builder()
                                .id(supplier.getId())
                                .taxId(supplier.getTaxId())
                                .name(supplier.getName())
                                .phone(supplier.getPhone())
                                .email(supplier.getEmail())
                                .address(supplier.getAddress())
                                .active(supplier.isActive())
                                .createdAt(supplier.getCreatedAt())
                                .updatedAt(supplier.getUpdatedAt())
                                .userId(supplier.getUser() != null ? supplier.getUser().getId() : null)
                                .build())
                        .build())
                .orElse(SupplierDetailResponseDto.builder()
                        .success(false)
                        .message("Supplier not found")
                        .data(null)
                        .build());
    }
}