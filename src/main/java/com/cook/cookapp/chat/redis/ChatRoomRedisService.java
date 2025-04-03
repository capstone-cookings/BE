package com.cook.cookapp.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // 메시지를 읽은 사용자 기록 (중복 방지-redis기능 최적화)
    public void markAsRead(Long messageId, Long userId) {
        String redisKey = key(messageId);
        Boolean alreadyRead = redisTemplate.opsForSet().isMember(redisKey, userId.toString());

        if (!Boolean.TRUE.equals(alreadyRead)) {
            redisTemplate.opsForSet().add(redisKey, userId.toString());
        }
    }

    // 해당 메시지를 읽은 사람 수 반환
    public int getReadCount(Long messageId) {
        Long size = redisTemplate.opsForSet().size(key(messageId));
        return size != null ? size.intValue() : 0;
    }

    // 해당 메시지를 읽은 사용자가 있는지 확인
    public boolean hasRead(Long messageId, Long userId) {
        String key = getReadKey(messageId);
        return redisTemplate.opsForSet().isMember(key,  userId.toString());
    }

    // 해당 메시지를 읽은 사용자 목록 반환
    private String getReadKey(Long messageId) {
        return "chat:read:" + messageId;
    }

    // 해당 메시지를 읽지 않은 사용자 수 반환
    public int getUnreadCount(Long messageId, List<Long> allUserIds) {
        String key = getReadKey(messageId);
        Long readCount = redisTemplate.opsForSet().size(key);
        return (int) (allUserIds.size() - (readCount != null ? readCount : 0));
    }
}
