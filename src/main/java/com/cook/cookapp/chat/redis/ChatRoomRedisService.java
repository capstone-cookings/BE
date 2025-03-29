package com.cook.cookapp.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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

    private String getKey(Long roomId) {
        return "chat:room:" + roomId + ":members";
    }
    //채팅방 입장 시 Redis Set에 사용자 ID 추가
    public void enterRoom(Long roomId, Long userId) {
        redisTemplate.opsForSet().add(getKey(roomId), userId.toString());
    }
    //채팅방 퇴장 시 Redis Set에서 사용자 ID 제거
    public void leaveRoom(Long roomId, Long userId) {
        redisTemplate.opsForSet().remove(getKey(roomId), userId.toString());
    }
    //채팅방의 현재 참여자(userId 목록)를 반환
    public List<Long> getMembers(Long roomId) {
        Set<String> members = redisTemplate.opsForSet().members(getKey(roomId));
        if (members == null) return List.of();
        return members.stream().map(Long::parseLong).toList();
    }
}
