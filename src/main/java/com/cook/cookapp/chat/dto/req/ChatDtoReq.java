package com.cook.cookapp.chat.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class ChatDtoReq {
    @Getter
    public static class ChatRoomCreateRequest {
        @NotBlank
        private String name; // 채팅방 이름

        @Min(value = 2)
        private int maxParticipants; // 최대 참여자 수
    }

    @Getter
    public static class ChatMessageRequest {
        @NotBlank
        private String content; // 보낼 메시지
    }
}
