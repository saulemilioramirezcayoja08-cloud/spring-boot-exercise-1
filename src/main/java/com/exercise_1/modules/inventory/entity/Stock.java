package com.exercise_1.modules.inventory.entity;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.catalog.entity.Product;
import com.exercise_1.modules.catalog.entity.Warehouse;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stocks")
public class Stock {

    @EmbeddedId
    private StockId id;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @MapsId("warehouseId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    @Min(value = 0)
    private Integer quantity;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}