package com.cook.cookapp.chat.socket.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageSocketRequest {
    private Long roomId;
    private String content;
}
