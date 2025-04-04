package com.cook.cookapp.chat.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ChatDtoRes {

    @Getter
    @Builder
    public static class ChatRoomResponse {
        private Long roomId; // 채팅방 ID
        private String name; //채팅방 이름
        private int currentParticipants; //현재 참여자 수
        private int maxParticipants; //최대 참여자 수
        private boolean isActive; //활성화 여부
    }

    @Getter
    @Builder
    public static class ChatRoomListItemResponse {
        private Long roomId; // 채팅방 ID
        private String name; //채팅방 이름
        private int currentParticipants; //현재 참여자 수

        private String lastMessage; //가장 최근 메시지
        private LocalDateTime lastMessageTime;//가장 최근 메시지 시간

        private int unreadCount; //안 읽은 메시지 수
    }

    @Getter
    @Builder
    public static class ChatMessageResponse {
        private Long messageId; // 메시지 ID
        private Long senderId; // 보낸 사람 ID
        private String senderNickname; // 보낸 사람 닉네임
        private String content; // 메시지 내용
        private LocalDateTime sentAt; // 보낸 시간
        private boolean isRead; // 읽음 여부 (나 기준)
        private int unreadCount; // 안 읽은 사람 수
    }

    @Getter
    @Builder
    public static class ChatUnreadBroadcast {
        private Long messageId;
        private int unreadCount; // 안 읽은 사람 수
        private Long roomId;         // 어떤 채팅방의 메시지인지 명시
        private boolean isMyMessage; // 본인이 보낸 메시지 여부 (프론트에서 UI 분기 용도)
    }

    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class UnreadCountResponse {
        private Long messageId;
        private int unreadCount;
    }

    @Getter
    @AllArgsConstructor(staticName = "of")
    public class ChatRoomMemberListResponse {
        private String nickname;
        private String imageUrl; // 프로필 이미지 URL
        private boolean isHost; // 방장 여부
    }
}
