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
 * 채팅 알림 엔티티
 * - 사용자가 채팅방에 없는 동안 도착한 메시지 알림 저장
 * - 읽지 않은 메시지 수 또는 FCM 알림 등과 연계
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 받는 사용자
    private Long receiverId;

    // 어떤 채팅방의 메시지인지
    private Long roomId;

    // 미리보기 (최근 메시지 내용 일부)
    private String contentPreview;

    // 알림 확인 여부
    private boolean isRead = false;

    private LocalDateTime notifiedAt;

    public static ChatNotification create(Long receiverId, Long roomId, String contentPreview) {
        ChatNotification n = new ChatNotification();
        n.receiverId = receiverId;
        n.roomId = roomId;
        n.contentPreview = contentPreview;
        n.notifiedAt = LocalDateTime.now();
        return n;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}

