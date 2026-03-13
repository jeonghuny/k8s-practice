package com.ordersystem.order.ordering.feignclients;

import com.ordersystem.order.ordering.dto.OrderCreateDto;
import com.ordersystem.order.ordering.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

// name부분은 eureka에 등록된 application name을 의미
// url부분은 k8s의 서비스명
@Profile("local")
@FeignClient(name = "product-service", url="${product.service.url:}")
public interface ProductFeignClient {

    @GetMapping("/product/detail/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);

    @PutMapping("/product/updatestock")
    void updateStockQuantity(@RequestBody OrderCreateDto dto);
}