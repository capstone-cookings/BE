package com.cook.cookapp.chat.socket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatEnterLeaveRequest {
    private Long roomId;
}
