package com.cook.cookapp.chat.socket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatReadEventRequest {
    private Long roomId;
}