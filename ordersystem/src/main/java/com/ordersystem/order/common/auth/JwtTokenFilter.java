package com.ordersystem.order.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtTokenFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private String st_secret_key;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        String bearerToken =req.getHeader("Authorization");

        try {
            if (bearerToken == null) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            } else {
                String token = bearerToken.substring(7);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(st_secret_key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
                Authentication authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch(Exception e){
//            실제 에러가 아닌 요소들은 로그를 찍을 필요 없으므로 아래 로그는 주석처리
//            e.printStackTrace();
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
