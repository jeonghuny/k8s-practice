package com.ordersystem.order.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductStockUpdateDto {
    private Long productId;
    private Integer productCount;

}
