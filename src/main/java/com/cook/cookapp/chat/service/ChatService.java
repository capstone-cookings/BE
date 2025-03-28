package com.cook.cookapp.chat.service;

import com.cook.cookapp.chat.dto.req.ChatDtoReq;
import com.cook.cookapp.chat.dto.res.ChatDtoRes;
import com.cook.cookapp.chat.socket.ChatMessageSocketRequest;

import java.util.List;

public interface ChatService {
    ChatDtoRes.ChatRoomResponse createChatRoom(Long userId, ChatDtoReq.ChatRoomCreateRequest request);
    ChatDtoRes.ChatRoomResponse getRoomById(Long roomId);
    ChatDtoRes.ChatRoomResponse joinRoom(Long userId, Long roomId);
    List<ChatDtoRes.ChatRoomListItemResponse> getMyChatRooms(Long userId); // 참여한 방 목록만
    List<ChatDtoRes.ChatMessageResponse> getMessagesByRoomId(Long userId, Long roomId);
    ChatDtoRes.ChatMessageResponse sendMessage(Long userId, Long roomId, ChatDtoReq.ChatMessageRequest request);
    int markMessagesAsRead(Long userId, Long roomId);
    ChatDtoRes.ChatMessageResponse saveWebSocketMessage(ChatMessageSocketRequest request);

}

