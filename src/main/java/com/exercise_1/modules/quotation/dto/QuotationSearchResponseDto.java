package com.exercise_1.modules.quotation.dto;

import com.exercise_1.modules.quotation.entity.QuotationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationSearchResponseDto {

    private Boolean success;
    private String message;
    private List<QuotationData> data;
    private PaginationMetadata pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotationData {
        private Long id;
        private String number;
        private QuotationStatus status;
        private String username;
        private BigDecimal totalAmount;
        private Long itemCount;
        private Instant createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationMetadata {
        private Integer currentPage;
        private Integer totalPages;
        private Long totalElements;
        private Integer pageSize;
        private Boolean hasNext;
        private Boolean hasPrevious;
    }
}