package com.exercise_1.modules.inventory.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StockId implements Serializable {
    private Long productId;
    private Long warehouseId;
}