package com.cook.cookapp.chat.service;

import com.cook.cookapp.chat.dto.req.ChatDtoReq;
import com.cook.cookapp.chat.dto.res.ChatDtoRes;
import com.cook.cookapp.chat.entity.ChatMessage;
import com.cook.cookapp.chat.entity.ChatRoom;

import java.util.List;

public interface ChatService {
    ChatDtoRes.ChatRoomResponse createChatRoom(Long userId, ChatDtoReq.ChatRoomCreateRequest request);
    ChatDtoRes.ChatRoomResponse getRoomById(Long roomId);
    ChatDtoRes.ChatRoomResponse joinRoom(Long userId, Long roomId);
    List<ChatDtoRes.ChatRoomListItemResponse> getMyChatRooms(Long userId); // 참여한 방 목록만
    List<ChatDtoRes.ChatMessageResponse> getMessagesByRoomId(Long userId, Long roomId);
    ChatDtoRes.ChatMessageResponse sendMessage(Long userId, Long roomId, ChatDtoReq.ChatMessageRequest request);
    List<ChatMessage> markMessagesAsRead(Long userId, Long roomId);
    ChatDtoRes.ChatMessageResponse saveWebSocketMessage(Long roomId, Long senderId, String senderNickname, String content);
    void exitChatRoom(Long userId, Long roomId);
    void closeRoom(Long roomId, Long userId);
    ChatRoom getChatRoomEntity(Long roomId);
    List<ChatDtoRes.ChatRoomMemberListResponse> getRoomParticipants(Long roomId);
}

