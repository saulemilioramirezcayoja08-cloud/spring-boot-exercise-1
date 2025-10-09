package com.exercise_1.modules.quotation.controller;

import com.exercise_1.modules.quotation.dto.*;
import com.exercise_1.modules.quotation.service.QuotationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    @PostMapping
    public ResponseEntity<QuotationResponseDto> createQuotation(@Valid @RequestBody QuotationCreateDto dto) {
        QuotationResponseDto response = quotationService.create(dto);

        if (response.getSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<QuotationResponseDto> confirmQuotation(@PathVariable Long id, @Valid @RequestBody QuotationConfirmDto dto) {
        QuotationResponseDto response = quotationService.confirm(id, dto);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<QuotationResponseDto> cancelQuotation(@PathVariable Long id, @Valid @RequestBody QuotationCancelDto dto) {
        QuotationResponseDto response = quotationService.cancel(id, dto);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }
    }

    @GetMapping("/search")
    public ResponseEntity<QuotationSearchResponseDto> search(
            @RequestParam(required = false) String number,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        QuotationSearchResponseDto response = quotationService.search(number, status, username, dateFrom, dateTo, page, size);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}