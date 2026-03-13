package com.ordersystem.order.ordering.controller;


import com.ordersystem.order.ordering.dto.OrderCreateDto;
import com.ordersystem.order.ordering.dto.OrderListDto;
import com.ordersystem.order.ordering.service.OrderingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordering")
public class OrderingController {
    private final OrderingService orderingService;

    public OrderingController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody List<OrderCreateDto> orderCreateDtoList
                                    , @RequestHeader("X-User-Email")String email) {
        Long id = orderingService.createFeign(orderCreateDtoList, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/list")
    public ResponseEntity<?> findAll(){
        List<OrderListDto> orderListDtoList = orderingService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(orderListDtoList);
    }

    @GetMapping("/myorders")
    public ResponseEntity<?> myOrders(@RequestHeader("X-User-Email")String email){
        List<OrderListDto> orderListDtoList  = orderingService.myorders(email);
        return ResponseEntity.status(HttpStatus.OK).body(orderListDtoList);
    }

}