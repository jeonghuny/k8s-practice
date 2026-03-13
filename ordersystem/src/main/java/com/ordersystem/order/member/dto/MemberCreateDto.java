package com.ordersystem.order.member.dto;


import com.ordersystem.order.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberCreateDto {
    @NotBlank(message = "이름이 비어있으면 안됩니다.")
    private String name;
    @NotBlank(message = "이메일이 비어있으면 안됩니다.")
    private String email;
    @NotBlank(message = "비밀번호가 비어있으면 안됩니다.")
    private String password;

    public Member toEntity(String encordedPassword){
        return Member.builder()
                .name(this.name)
                .email(this.email)
                .password(encordedPassword)
                .build();
    }
}
