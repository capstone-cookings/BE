package com.cook.cookapp.chat.socket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageSocketRequest {
    private Long roomId;
    private Long senderId;
    private String senderNickname;
    private String content;
}
