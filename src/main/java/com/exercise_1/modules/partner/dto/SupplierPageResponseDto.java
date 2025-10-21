package com.exercise_1.modules.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPageResponseDto {
    private Boolean success;
    private String message;
    private PageData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageData {
        private List<SupplierListItem> content;
        private PaginationInfo pagination;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplierListItem {
        private Long id;
        private String taxId;
        private String name;
        private String phone;
        private String email;
        private String address;
        private Boolean active;
        private Instant createdAt;
        private Instant updatedAt;
        private Long userId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private Integer currentPage;
        private Integer pageSize;
        private Long totalElements;
        private Integer totalPages;
        private Boolean isFirst;
        private Boolean isLast;
        private Boolean hasNext;
        private Boolean hasPrevious;
    }
}