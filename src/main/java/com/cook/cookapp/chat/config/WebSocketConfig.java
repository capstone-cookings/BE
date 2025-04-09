package com.cook.cookapp.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }

    // 메시지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // 구독 prefix (SimpleBroker: 메시지 브로커- Spring 내장)
        registry.setApplicationDestinationPrefixes("/pub"); // 발행 prefix
    }

    // 소켓 연결 Endpoint 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        System.out.println("[서버] WebSocket 엔드포인트 등록됨!");
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*");
//                .withSockJS();
    }
}
