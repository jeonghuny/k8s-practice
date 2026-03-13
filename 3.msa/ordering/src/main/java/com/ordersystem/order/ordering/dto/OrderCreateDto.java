package com.ordersystem.order.ordering.dto;

import com.ordersystem.order.ordering.domain.Ordering;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderCreateDto {
    @NotBlank(message = "상품 아이디를 입력해주세요")
    private Long productId;
    @NotBlank(message = "상품 수량을 입력해주세요")
    private Integer productCount;

}
