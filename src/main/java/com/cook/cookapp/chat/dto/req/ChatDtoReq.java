package com.cook.cookapp.chat.dto.req;

import lombok.Getter;

public class ChatDtoReq {
    @Getter
    public static class ChatRoomCreateRequest {
        private String name; // 채팅방 이름
    }

}
