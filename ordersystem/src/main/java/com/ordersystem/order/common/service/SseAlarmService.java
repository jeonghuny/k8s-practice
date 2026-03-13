package com.ordersystem.order.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordersystem.order.common.dtos.SseMessageDto;
import com.ordersystem.order.common.repository.SseEmitterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;

@Component
public class SseAlarmService implements MessageListener {
    private final SseEmitterRegistry sseEmitterRegistry;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String,String> redisTemplate;
    @Autowired
    public SseAlarmService(SseEmitterRegistry sseEmitterRegistry, ObjectMapper objectMapper, @Qualifier("ssePubSub") RedisTemplate<String, String> redisTemplate) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    public void sendMessage(String receiver, String sender, String message) {

        //보낼 알림 DTO 생성
        //(받는사람, 보낸사람, 메시지)
        SseMessageDto dto = SseMessageDto.builder()
                .receiver(receiver)
                .sender(sender)
                .message(message)
                .build();
        try {
            //지금 이 서버에 해당 관리자가 SSE 연결 중인지 확인
            SseEmitter sseEmitter = sseEmitterRegistry.getEmitter(receiver);
            String data = objectMapper.writeValueAsString(dto);
//            만약에 emitter객체가 현재 서버에 있으면, 바로 알림 발송, 그렇지 않으면, redis pub/sub 활용.
//            분기 처리 (이게 멀티서버 핵심)
            if(sseEmitter != null){
//                👉 바로 브라우저로 알림 전송 (Redis 안 거침 )
                sseEmitter.send(SseEmitter.event().name("ordered").data(data));
//                사용자가 새로고침후에 알림메시지를 조회하려면 DB에 추가적으로 저장 필요.
            }else{
//                👉 관리자가 다른 서버에 접속 중이면 Redis로 전파
                redisTemplate.convertAndSend("order-channel",data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
        //Redis가 메시지 보내면 자동 실행됨(Redis 메시지 수신 → SSE 전송)
    public void onMessage(Message message, byte[] pattern) {
//        message : 실질적으로 메시지가 담겨 있는 객체
//        pattern : 채널명
//        추후 여러개의 채널에 각기 메시지를 publish하고 subscribe할 경우, 채널명으로 분기처리 가능
        String channelName = new String(pattern);
        try {
            //Redis에서 받은 JSON → DTO 변환
            SseMessageDto dto = objectMapper.readValue(message.getBody(), SseMessageDto.class);
            String data = objectMapper.writeValueAsString(dto); //받아온 메세지를 다시 직렬화시킴
            //이 서버에 수신자가 연결돼 있는지 확인
            SseEmitter sseEmitter = sseEmitterRegistry.getEmitter(dto.getReceiver());
//            해당 서버에 receiver에 emitter객체가 있으면 send 하겠다
            if(sseEmitter != null){
                //👉 연결돼 있으면 브라우저로 SSE 전송
                sseEmitter.send(SseEmitter.event().name("ordered").data(data));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
