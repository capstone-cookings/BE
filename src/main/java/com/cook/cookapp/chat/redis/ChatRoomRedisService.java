package com.cook.cookapp.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 채팅방 참여자 실시간 관리를 위한 Redis 서비스
 * - Redis Set을 활용하여 채팅방 내 현재 접속 중인 유저 ID를 저장/삭제/조회
 * - WebSocket 입장/퇴장 이벤트와 연동하여 사용
 */
@Service
@RequiredArgsConstructor
public class ChatRoomRedisService {

    @Qualifier("chatRedisTemplate")
    private final StringRedisTemplate redisTemplate;

    private String key(Long messageId) {
        return "chat:read:" + messageId;
    }
    // 메시지를 읽은 사용자 기록
    public void markAsRead(Long messageId, Long userId) {
        redisTemplate.opsForSet().add("chat:read:" + messageId, userId.toString());
    }
    // 해당 메시지를 읽은 사람 수 반환
    public int getReadCount(Long messageId) {
        Long size = redisTemplate.opsForSet().size("chat:read:" + messageId);
        return size != null ? size.intValue() : 0;
    }
}
