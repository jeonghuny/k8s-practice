package com.ordersystem.order.ordering.service;

// name 부분은 eureka에 등록된 application name을 의미

import com.ordersystem.order.common.service.SseAlarmService;
import com.ordersystem.order.ordering.domain.OrderDetail;
import com.ordersystem.order.ordering.domain.Ordering;
import com.ordersystem.order.ordering.dto.OrderCreateDto;
import com.ordersystem.order.ordering.dto.OrderListDto;
import com.ordersystem.order.ordering.dto.ProductDto;
import com.ordersystem.order.ordering.feignclients.ProductFeignClient;
import com.ordersystem.order.ordering.repository.OrderDetailRepository;
import com.ordersystem.order.ordering.repository.OrderingRepository;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final SseAlarmService sseAlarmService;
    private final RestTemplate restTemplate;
    private final ProductFeignClient productFeignClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderingService(OrderingRepository orderingRepository, OrderDetailRepository orderDetailRepository, SseAlarmService sseAlarmService, RestTemplate restTemplate, ProductFeignClient productFeignClient, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderingRepository = orderingRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.sseAlarmService = sseAlarmService;
        this.restTemplate = restTemplate;
        this.productFeignClient = productFeignClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Long create( List<OrderCreateDto> orderCreateDtoList, String email){
        Ordering ordering = Ordering.builder()
                .memberEmail(email)
                .build();
        orderingRepository.save(ordering);
        for (OrderCreateDto dto : orderCreateDtoList){
//            1.재고조회요청(동기요청-http요청)
//            http://localhost:8080/product-service : apigateway을 통한 호출
//            http://product-service : eureka에게 질의 후 product-service 직접 호출
            String endpoint1 = "http://product-service/product/detail/" + dto.getProductId();
            HttpHeaders headers = new HttpHeaders();
//            HttpEntity : header + body
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            ResponseEntity <ProductDto> responseEntity = restTemplate.exchange(endpoint1, HttpMethod.GET, httpEntity, ProductDto.class);
            ProductDto product = responseEntity.getBody();
            if(product.getStockQuantity() < dto.getProductCount()){
                throw new IllegalArgumentException("재고가 부족합니다.");
            }
//            2.주문발생
            OrderDetail orderDetail = OrderDetail.builder()
                    .ordering(ordering)
                    .productName(product.getName())
                    .productId(dto.getProductId())
                    .quantity(dto.getProductCount())
                    .build();
            ordering.getOrderDetailList().add(orderDetail);
            orderDetailRepository.save(orderDetail);
//            3.재고감소요청(동기-http요청/비동기-이벤트기반 모두 가능) // 비동기 잘 안쓰는것 같음 실무에서
            String endpoint2 = "http://product-service/product/updatestock";
            HttpHeaders headers2 = new HttpHeaders();
            headers2.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity : header + body
            HttpEntity<OrderCreateDto> httpEntity2 = new HttpEntity<>(dto, headers2);
            restTemplate.exchange(endpoint2, HttpMethod.PUT, httpEntity2, void.class);
        }
        return ordering.getId();
    }

    public Long createFeign( List<OrderCreateDto> orderCreateDtoList, String email){
        Ordering ordering = Ordering.builder()
                .memberEmail(email)
                .build();
        orderingRepository.save(ordering);

        for (OrderCreateDto dto : orderCreateDtoList){
            ProductDto product = productFeignClient.getProductById(dto.getProductId());
            if(product.getStockQuantity() < dto.getProductCount()){
                throw new IllegalArgumentException("재고가 부족합니다");
            }
            OrderDetail orderDetail = OrderDetail.builder()
                    .ordering(ordering)
                    .productName(product.getName())
                    .productId(dto.getProductId())
                    .quantity(dto.getProductCount())
                    .build();
            orderDetailRepository.save(orderDetail);
            //        feign을 사용한 동기적 재고 감소 요청
            //        productFeignClient.updateStockQuantity(dto);
            //        kafka를 활용한 비동기적 재고 감소
            kafkaTemplate.send("stock-update-topic", dto);
        }

        return ordering.getId();
    }


    @Transactional(readOnly = true)
    public List<OrderListDto> findAll(){
        return orderingRepository.findAll().stream().map(o->OrderListDto.fromEntity(o)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderListDto> myorders(String email){
        return orderingRepository.findAllByMemberEmail(email).stream().map(o->OrderListDto.fromEntity(o)).collect(Collectors.toList());
    }
}