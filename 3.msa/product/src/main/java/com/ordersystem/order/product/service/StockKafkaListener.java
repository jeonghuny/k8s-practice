package com.ordersystem.order.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordersystem.order.product.dto.ProductStockUpdateDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StockKafkaListener {
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public StockKafkaListener(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    // stock-update-topic에 메시지가 오면 자동으로 이 메서드 실행
    @KafkaListener(topics = "stock-update-topic", containerFactory = "kafkaListener")
    public void stockConsumer(String mesasge) throws JsonProcessingException {
        System.out.println("====kafka listener start=====");
        ProductStockUpdateDto dto = objectMapper.readValue(mesasge, ProductStockUpdateDto.class);
        productService.updateStock(dto);
    }
}
