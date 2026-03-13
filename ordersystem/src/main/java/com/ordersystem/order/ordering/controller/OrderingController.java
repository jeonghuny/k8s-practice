package com.ordersystem.order.ordering.controller;


import com.ordersystem.order.ordering.dto.OrderCreateDto;
import com.ordersystem.order.ordering.dto.OrderListDto;
import com.ordersystem.order.ordering.service.OrderingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<?> create(@RequestBody List<OrderCreateDto> orderCreateDtoList) {
        Long id = orderingService.create(orderCreateDtoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAll(){
        List<OrderListDto> orderListDtoList = orderingService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(orderListDtoList);
    }

    @GetMapping("/myorders")
    public ResponseEntity<?> myOrders(){
        List<OrderListDto> orderListDtoList  = orderingService.myorders();
        return ResponseEntity.status(HttpStatus.OK).body(orderListDtoList);
    }

}