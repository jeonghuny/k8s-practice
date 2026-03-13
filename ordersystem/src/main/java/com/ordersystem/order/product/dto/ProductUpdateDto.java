package com.ordersystem.order.product.dto;

import com.ordersystem.order.member.domain.Member;
import com.ordersystem.order.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductUpdateDto {
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
//    이미지 수정은 일반적으로 별도의 api로 처리(성능이슈 S3에 계속 올려줘야댐)
    private MultipartFile productImage;
}