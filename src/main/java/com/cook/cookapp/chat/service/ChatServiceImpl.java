package com.cook.cookapp.chat.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.chat.dto.req.ChatDtoReq;
import com.cook.cookapp.chat.dto.res.ChatDtoRes;
import com.cook.cookapp.chat.entity.ChatRoom;
import com.cook.cookapp.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;

    @Override
    public ChatDtoRes.ChatRoomResponse createChatRoom(Long userId, ChatDtoReq.ChatRoomCreateRequest request) {
        ChatRoom chatRoom = ChatRoom.create(request.getName(), userId);
        ChatRoom savedRoom = chatRepository.save(chatRoom);
        return ChatDtoRes.ChatRoomResponse.builder()
                .roomId(savedRoom.getId())
                .name(savedRoom.getName())
                .currentParticipants(savedRoom.getCurrentParticipants())
                .isActive(savedRoom.isActive())
                .build();
    }
    @Override
    public List<ChatDtoRes.ChatRoomResponse> getAllRooms() {
        return chatRepository.findAll().stream()
                .map(room -> ChatDtoRes.ChatRoomResponse.builder()
                        .roomId(room.getId())
                        .name(room.getName())
                        .currentParticipants(room.getCurrentParticipants())
                        .isActive(room.isActive())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ChatDtoRes.ChatRoomResponse getRoomById(Long roomId) {
        ChatRoom room = chatRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));
        return ChatDtoRes.ChatRoomResponse.builder()
                .roomId(room.getId())
                .name(room.getName())
                .currentParticipants(room.getCurrentParticipants())
                .isActive(room.isActive())
                .build();
    }

    @Override
    public ChatDtoRes.ChatRoomResponse joinRoom(Long roomId) {
        ChatRoom room = chatRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));
        room.increaseParticipants();
        return ChatDtoRes.ChatRoomResponse.builder()
                .roomId(room.getId())
                .name(room.getName())
                .currentParticipants(room.getCurrentParticipants())
                .isActive(room.isActive())
                .build();
    }
}
