package com.cook.cookapp.chat.config;

import com.cook.cookapp.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 헤더 파싱
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        // 연결 요청 또는 메시지 전송 시
        if (accessor.getCommand() != null) {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders != null && !authHeaders.isEmpty()) {
                String bearerToken = authHeaders.get(0);
                String token = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;

                // JWT 검증 및 SecurityContext 등록
                if (jwtTokenProvider.processTokenAndSetAuthContext(token)) {
                    log.info("[WebSocket 인증 성공] userId={}", jwtTokenProvider.getUserIdInToken(token));
                } else {
                    log.warn("[WebSocket 인증 실패] 잘못된 토큰");
                }
            }
        }

        return message;
    }
}
