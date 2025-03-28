package com.cook.cookapp.chat.dto.req;

import lombok.Getter;

public class ChatDtoReq {
    @Getter
    public static class ChatRoomCreateRequest {
        private String name; // 채팅방 이름
        private int maxParticipants; // 최대 참여자 수
    }

    @Getter
    public static class ChatMessageRequest {
        private String content; // 보낼 메시지
    }

}
