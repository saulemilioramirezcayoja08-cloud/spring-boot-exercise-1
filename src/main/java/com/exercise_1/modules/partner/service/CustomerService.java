package com.exercise_1.modules.partner.service;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.partner.dto.CustomerCreateDto;
import com.exercise_1.modules.partner.dto.CustomerDetailResponseDto;
import com.exercise_1.modules.partner.dto.CustomerResponseDto;
import com.exercise_1.modules.partner.entity.Customer;
import com.exercise_1.modules.partner.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Transactional
    public CustomerResponseDto create(CustomerCreateDto dto) {

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return CustomerResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        if (dto.getTaxId() != null && customerRepository.existsByTaxId(dto.getTaxId())) {
            return CustomerResponseDto.builder()
                    .success(false)
                    .message("Customer with Tax ID " + dto.getTaxId() + " already exists")
                    .data(null)
                    .build();
        }

        try {
            Customer customer = Customer.builder()
                    .taxId(dto.getTaxId())
                    .name(dto.getName())
                    .phone(dto.getPhone())
                    .email(dto.getEmail())
                    .address(dto.getAddress())
                    .active(true)
                    .user(dto.getUserId() != null ?
                            User.builder().id(dto.getUserId()).build() : null)
                    .build();

            Customer savedCustomer = customerRepository.save(customer);

            return CustomerResponseDto.builder()
                    .success(true)
                    .message("Customer created successfully")
                    .data(CustomerResponseDto.CustomerData.builder()
                            .id(savedCustomer.getId())
                            .taxId(savedCustomer.getTaxId())
                            .name(savedCustomer.getName())
                            .email(savedCustomer.getEmail())
                            .build())
                    .build();

        } catch (Exception e) {
            return CustomerResponseDto.builder()
                    .success(false)
                    .message("Error creating customer: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public CustomerDetailResponseDto findById(Long id) {
        return customerRepository.findById(id)
                .map(customer -> CustomerDetailResponseDto.builder()
                        .success(true)
                        .message("Customer found successfully")
                        .data(CustomerDetailResponseDto.CustomerDetailData.builder()
                                .id(customer.getId())
                                .taxId(customer.getTaxId())
                                .name(customer.getName())
                                .phone(customer.getPhone())
                                .email(customer.getEmail())
                                .address(customer.getAddress())
                                .active(customer.isActive())
                                .createdAt(customer.getCreatedAt())
                                .updatedAt(customer.getUpdatedAt())
                                .userId(customer.getUser() != null ? customer.getUser().getId() : null)
                                .build())
                        .build())
                .orElse(CustomerDetailResponseDto.builder()
                        .success(false)
                        .message("Customer not found")
                        .data(null)
                        .build());
    }
}