package com.ordersystem.order.common.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//사용자별 SSE 연결 객체를 관리하는 레지스트리(저장소).
@Component
public class SseEmitterRegistry {
//    SseEmitter객체는 사용자의 연결정보(ip, macaddress 등)을 의미
//    ConcurrentHashMap은 Thread-Safe한 map(동시성 이슈 발생 x)
    //SSE는 비동기 통신이기 때문에 동시에 여러 사용자가 연결될 수 있음 → Thread-safe Map 필요
    //동시에 여러 사용자가 SSE 연결을 시도할 수 있기 때문에, Map이 안전하게 여러 스레드에서 접근 가능해야 한다는 의미
    private Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    public void addSseEmitter(String email, SseEmitter sseEmitter){
        this.emitterMap.put(email, sseEmitter);
        System.out.println(this.emitterMap.size());
    }

    public SseEmitter getEmitter(String email){
        return this.emitterMap.get(email);
    }

    public void removeEmitter(String email){
        this.emitterMap.remove(email);
        System.out.println(this.emitterMap.size());
    }
}
