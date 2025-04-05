package com.cook.cookapp.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 엔티티
 * - 사용자 간 채팅 메시지 내용을 저장
 * - 발신자, 내용, 시간, 읽음 여부 포함
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속된 채팅방 ID
    private Long roomId;

    // 보낸 사용자 ID
    private Long senderId;

    // 보낸 사람 닉네임 (UI 렌더링에 유용)
    private String senderNickname;

    // 메시지 내용
    private String content;

    // 메시지 보낸 시간
    private LocalDateTime sentAt;

    // 읽음 여부
    private boolean isRead = false;

    public static ChatMessage create(Long roomId, Long senderId, String senderNickname, String content) {
        ChatMessage msg = new ChatMessage();
        msg.roomId = roomId;
        msg.senderId = senderId;
        msg.senderNickname = senderNickname;
        msg.content = content;
        msg.sentAt = LocalDateTime.now();
        return msg;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}

