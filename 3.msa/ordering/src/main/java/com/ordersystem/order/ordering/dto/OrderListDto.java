package com.ordersystem.order.ordering.dto;

import com.ordersystem.order.ordering.domain.OrderDetail;
import com.ordersystem.order.ordering.domain.OrderStatus;
import com.ordersystem.order.ordering.domain.Ordering;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderListDto {
    private Long id;
    private String memberEmail;
    private OrderStatus orderStatus;
    private List<OrderDetailDto> orderDetails;
    public static OrderListDto fromEntity(Ordering ordering){
        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
        for(OrderDetail orderDetail : ordering.getOrderDetailList()){
            orderDetailDtos.add(OrderDetailDto.fromEntity(orderDetail));
        }
        OrderListDto orderListDto = OrderListDto.builder()
                .id(ordering.getId())
                .orderDetails(orderDetailDtos)
                .memberEmail(ordering.getMemberEmail())
                .orderStatus(ordering.getOrderStatus())
                .build();
        return orderListDto;
    }
}
