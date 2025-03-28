package com.cook.cookapp.chat.entity;

import com.cook.cookapp.global.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 채팅방 이름 (예: '떡볶이 공동구매방')

    private Long hostUserId; // 방 만든 사람 ID(userId)

    private boolean isActive = true; // 채팅방 활성화 여부 (나중에 방 종료 등 처리용)

    // 현재 참여자 수 캐싱용
    private int currentParticipants;

    // === 채팅방 생성 메서드 ===
    public static ChatRoom create(String name, Long hostUserId) {
        ChatRoom room = new ChatRoom();
        room.name = name;
        room.hostUserId = hostUserId;
        room.currentParticipants = 1;
        return room;
    }

    public void increaseParticipants() {
        this.currentParticipants += 1;
    }

    public void decreaseParticipants() {
        this.currentParticipants = Math.max(0, this.currentParticipants - 1);
    }

    public void deactivate() {
        this.isActive = false;
    }
}

