package com.cook.cookapp.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 참여자 엔티티
 * - 사용자와 채팅방 간의 관계 (참여 여부 관리)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long roomId;

    public ChatRoomParticipant(Long userId, Long roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }
}
