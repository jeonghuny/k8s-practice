package com.ordersystem.order.member.dto;

import com.ordersystem.order.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MemberLoginReqDto {
    private String email;
    private String password;
}