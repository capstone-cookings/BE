package com.cook.cookapp.chat.socket;

import com.cook.cookapp.chat.redis.ChatRoomRedisService;
import com.cook.cookapp.chat.redis.RedisWebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketDisconnectListener {

    private final ChatRoomRedisService chatRoomRedisService;
    private final RedisWebSocketSessionManager sessionManager; // sessionId → userId, roomId 저장소

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        log.info("[WebSocket] 연결 종료 감지 - sessionId={}", sessionId);

        // 세션 ID로부터 유저 ID, 방 ID 추적
        Long userId = sessionManager.getUserId(sessionId);
        Long roomId = sessionManager.getRoomId(sessionId);

        if (userId != null && roomId != null) {
            // Redis 연결자 목록에서 제거
            chatRoomRedisService.removeConnectedUser(roomId, userId);
            log.info("[WebSocket] {} 유저 방 {} 퇴장 처리 완료", userId, roomId);

            // 세션 정리
            sessionManager.remove(sessionId);
        } else {
            log.warn("[WebSocket] sessionId={} 에 대한 유저/방 정보 없음", sessionId);
        }
    }
}