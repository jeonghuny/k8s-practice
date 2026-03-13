package com.ordersystem.order.common.controller;

import com.ordersystem.order.common.repository.SseEmitterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/sse")
public class SseController {

    private final SseEmitterRegistry sseEmitterRegistry;
    @Autowired
    public SseController(SseEmitterRegistry sseEmitterRegistry) {
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    @GetMapping("/connect")
//    싱글스레드로 객체 만들대 주의할것있음.
    public SseEmitter connect(@RequestHeader("X-User-Email")String email) throws IOException {
        System.out.println("connect start");
        SseEmitter sseEmitter = new SseEmitter(60*60*1000L); // 1시간 유효시간
        sseEmitterRegistry.addSseEmitter(email, sseEmitter);

        sseEmitter.send(SseEmitter.event().name("connect").data("연결완료"));
        //SSE는 HTTP 응답을 “계속 열어두는 스트리밍” 방식
        //서버가 HTTP response를 스트리밍 상태로 유지 //클라이언트는 이벤트를 받을 준비가 된 상태
        return sseEmitter;
    }

    @GetMapping("/disconnect")
    public void disconnect(@RequestHeader("X-User-Email")String email) throws IOException {
        System.out.println("disconnect start");
        sseEmitterRegistry.removeEmitter(email);
    }
}
