package com.cook.cookapp.chat.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.chat.dto.req.ChatDtoReq;
import com.cook.cookapp.chat.dto.res.ChatDtoRes;
import com.cook.cookapp.chat.entity.ChatMessage;
import com.cook.cookapp.chat.entity.ChatRoom;
import com.cook.cookapp.chat.entity.ChatRoomParticipant;
import com.cook.cookapp.chat.repository.ChatMessageRepository;
import com.cook.cookapp.chat.repository.ChatRoomParticipantRepository;
import com.cook.cookapp.chat.repository.ChatRoomRepository;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatDtoRes.ChatRoomResponse createChatRoom(Long userId, ChatDtoReq.ChatRoomCreateRequest request) {
        ChatRoom chatRoom = ChatRoom.create(request.getName(), userId, request.getMaxParticipants());
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 생성자 자동 참여 처리
        participantRepository.save(new ChatRoomParticipant(userId, savedRoom.getId()));

        return ChatDtoRes.ChatRoomResponse.builder()
                .roomId(savedRoom.getId())
                .name(savedRoom.getName())
                .currentParticipants(savedRoom.getCurrentParticipants())
                .isActive(savedRoom.isActive())
                .build();
    }

    @Override
    public ChatDtoRes.ChatRoomResponse getRoomById(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

        return ChatDtoRes.ChatRoomResponse.builder()
                .roomId(room.getId())
                .name(room.getName())
                .currentParticipants(room.getCurrentParticipants())
                .isActive(room.isActive())
                .build();
    }

    @Override
    public ChatDtoRes.ChatRoomResponse joinRoom(Long userId, Long roomId) {
        // 채팅방이 존재하는지 확인
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

        // 이미 참여 중인지 확인
        if (participantRepository.existsByUserIdAndRoomId(userId, roomId)) {
            return ChatDtoRes.ChatRoomResponse.builder()
                    .roomId(room.getId())
                    .name(room.getName())
                    .currentParticipants(room.getCurrentParticipants())
                    .maxParticipants(room.getMaxParticipants())
                    .isActive(room.isActive())
                    .build();
        }

        // 참여 인원이 초과되었는지 확인
        if (room.getCurrentParticipants() >= room.getMaxParticipants()) {
            throw new GeneralException(ErrorStatus.CHATROOM_FULL);
        }

        // 참여 처리
        participantRepository.save(new ChatRoomParticipant(userId, roomId));
        room.increaseParticipants();

        // 응답 반환
        return ChatDtoRes.ChatRoomResponse.builder()
                .roomId(room.getId())
                .name(room.getName())
                .currentParticipants(room.getCurrentParticipants())
                .maxParticipants(room.getMaxParticipants())
                .isActive(room.isActive())
                .build();
    }

    @Override
    public List<ChatDtoRes.ChatRoomListItemResponse> getMyChatRooms(Long userId) {
        List<ChatRoomParticipant> myParticipations = participantRepository.findAllByUserId(userId);
        List<ChatDtoRes.ChatRoomListItemResponse> result = new ArrayList<>();

        for (ChatRoomParticipant p : myParticipations) {
            ChatRoom room = chatRoomRepository.findById(p.getRoomId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

            // 마지막 메시지 조회
            Optional<ChatMessage> lastMessageOpt = chatMessageRepository.findTopByRoomIdOrderBySentAtDesc(room.getId());
            ChatMessage lastMessage = lastMessageOpt.orElse(null);

            // 내가 안 읽은 메시지 수 (내가 보낸 건 제외)
            int unreadCount = chatMessageRepository.countByRoomIdAndSenderIdNotAndIsReadFalse(room.getId(), userId);

            result.add(ChatDtoRes.ChatRoomListItemResponse.builder()
                    .roomId(room.getId())
                    .name(room.getName())
                    .currentParticipants(room.getCurrentParticipants())
                    .lastMessage(lastMessage != null ? lastMessage.getContent() : "(아직 메시지가 없습니다)")
                    .lastMessageTime(lastMessage != null ? lastMessage.getSentAt() : null)
                    .unreadCount(unreadCount)
                    .build());
        }

        return result;
    }

    @Override
    public List<ChatDtoRes.ChatMessageResponse> getMessagesByRoomId(Long userId, Long roomId) {
        // 채팅방 존재 여부 검증
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

        // 참여자 확인 (보안 처리)
        if (!participantRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_CHAT_ACCESS);
        }

        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);

        return messages.stream()
                .map(msg -> ChatDtoRes.ChatMessageResponse.builder()
                        .messageId(msg.getId())
                        .senderId(msg.getSenderId())
                        .senderNickname(msg.getSenderNickname())
                        .content(msg.getContent())
                        .sentAt(msg.getSentAt())
                        .isRead(msg.isRead()) // 읽음 여부 포함
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ChatDtoRes.ChatMessageResponse sendMessage(Long userId, Long roomId, ChatDtoReq.ChatMessageRequest request) {
        // 채팅방 검증
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

        // 참여자 권한 확인
        if (!participantRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_CHAT_ACCESS);
        }

        User user = userService.getUserById(userId);
        String nickname = user.getNickname();

        // 메시지 생성 및 저장
        ChatMessage message = ChatMessage.create(roomId, userId, nickname, request.getContent());
        ChatMessage saved = chatMessageRepository.save(message);

        return ChatDtoRes.ChatMessageResponse.builder()
                .messageId(saved.getId())
                .senderId(saved.getSenderId())
                .senderNickname(saved.getSenderNickname())
                .content(saved.getContent())
                .sentAt(saved.getSentAt())
                .isRead(saved.isRead())
                .build();
    }
    @Override
    public int markMessagesAsRead(Long userId, Long roomId) {
        // 채팅방 존재 여부 검증
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

        // 참여자 권한 확인
        if (!participantRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_CHAT_ACCESS);
        }

        // 읽음 처리
        return chatMessageRepository.markAllAsReadByUser(userId, roomId);
    }

}

