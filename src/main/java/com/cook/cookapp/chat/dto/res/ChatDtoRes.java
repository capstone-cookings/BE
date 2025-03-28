package com.cook.cookapp.chat.dto.res;

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
        private boolean isActive; //활성화 여부
    }

    @Getter
    @Builder
    public class ChatRoomListItemResponse {
        private Long roomId; // 채팅방 ID
        private String name; //채팅방 이름
        private int currentParticipants; //현재 참여자 수

        private String lastMessage; //가장 최근 메시지
        private LocalDateTime lastMessageTime;//가장 최근 메시지 시간

        private int unreadCount; //안 읽은 메시지 수
    }

}
