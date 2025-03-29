package com.cook.cookapp.chat.socket;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatRoomMemberListResponse {
    private Long roomId;
    private List<Long> memberIds;
}