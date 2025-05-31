package com.cook.cookapp.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class RedisWebSocketSessionManager {

    @Qualifier("chatRedisTemplate")
    private final StringRedisTemplate chatRedisTemplate;

    private HashOperations<String, String, String> hashOps;

    private static final String PREFIX = "session:";

    @PostConstruct
    public void init() {
        hashOps = chatRedisTemplate.opsForHash();
    }

    public void register(String sessionId, Long userId, Long roomId) {
        String key = PREFIX + sessionId;
        hashOps.put(key, "userId", String.valueOf(userId));
        hashOps.put(key, "roomId", String.valueOf(roomId));
        chatRedisTemplate.expire(key, java.time.Duration.ofHours(6)); // optional TTL
    }

    public Long getUserId(String sessionId) {
        String userId = hashOps.get(PREFIX + sessionId, "userId");
        return userId != null ? Long.valueOf(userId) : null;
    }

    public Long getRoomId(String sessionId) {
        String roomId = hashOps.get(PREFIX + sessionId, "roomId");
        return roomId != null ? Long.valueOf(roomId) : null;
    }

    public void remove(String sessionId) {
        chatRedisTemplate.delete(PREFIX + sessionId);
    }
}
