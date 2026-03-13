package com.ordersystem.order.product.domain;

import com.ordersystem.order.common.domain.BaseTimeEntity;
import com.ordersystem.order.member.domain.Member;
import com.ordersystem.order.product.dto.ProductUpdateDto;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
@Entity
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private Integer price;
    private Integer stockQuantity;
    private String imagePath;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT), nullable = false)
    private Member member;

    public void updateProfileImageUrl(String url){
        this.imagePath = url;
    }

    public void updateStockQuantity(int orderQuantity){
        this.stockQuantity = this.stockQuantity-orderQuantity;
    }

    public void updateProduct(ProductUpdateDto dto){
        this.name = dto.getName();
        this.category = dto.getCategory();
        this.stockQuantity = dto.getStockQuantity();
        this.price = dto.getPrice();

    }
}