package com.cook.cookapp.chat.service;

import com.cook.cookapp.chat.dto.req.ChatDtoReq;
import com.cook.cookapp.chat.dto.res.ChatDtoRes;

import java.util.List;

public interface ChatService {
    ChatDtoRes.ChatRoomResponse createChatRoom(Long userId, ChatDtoReq.ChatRoomCreateRequest request);

    List<ChatDtoRes.ChatRoomResponse> getAllRooms();

    ChatDtoRes.ChatRoomResponse getRoomById(Long roomId);

    ChatDtoRes.ChatRoomResponse joinRoom(Long roomId);
}
