package com.cook.cookapp.chat.socket.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatReadEventRequest {
    private Long roomId;
}