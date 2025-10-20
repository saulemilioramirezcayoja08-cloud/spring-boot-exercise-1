package com.exercise_1.modules.partner.controller;

import com.exercise_1.modules.partner.dto.CustomerCreateDto;
import com.exercise_1.modules.partner.dto.CustomerDetailResponseDto;
import com.exercise_1.modules.partner.dto.CustomerResponseDto;
import com.exercise_1.modules.partner.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@Valid @RequestBody CustomerCreateDto dto) {
        CustomerResponseDto response = customerService.create(dto);

        if (response.getSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if (response.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDetailResponseDto> getCustomerById(@PathVariable Long id) {
        CustomerDetailResponseDto response = customerService.findById(id);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}