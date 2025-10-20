package com.exercise_1.modules.partner.controller;

import com.exercise_1.modules.partner.dto.SupplierCreateDto;
import com.exercise_1.modules.partner.dto.SupplierDetailResponseDto;
import com.exercise_1.modules.partner.dto.SupplierResponseDto;
import com.exercise_1.modules.partner.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierResponseDto> createSupplier(@Valid @RequestBody SupplierCreateDto dto) {
        SupplierResponseDto response = supplierService.create(dto);

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
    public ResponseEntity<SupplierDetailResponseDto> getSupplierById(@PathVariable Long id) {
        SupplierDetailResponseDto response = supplierService.findById(id);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}