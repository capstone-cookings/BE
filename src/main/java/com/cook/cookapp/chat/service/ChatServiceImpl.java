package com.cook.cookapp.chat.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.chat.dto.req.ChatDtoReq;
import com.cook.cookapp.chat.dto.res.ChatDtoRes;
import com.cook.cookapp.chat.entity.ChatMessage;
import com.cook.cookapp.chat.entity.ChatRoom;
import com.cook.cookapp.chat.entity.ChatRoomParticipant;
import com.cook.cookapp.chat.redis.ChatRoomRedisService;
import com.cook.cookapp.chat.repository.ChatMessageRepository;
import com.cook.cookapp.chat.repository.ChatRoomParticipantRepository;
import com.cook.cookapp.chat.repository.ChatRoomRepository;
import com.cook.cookapp.global.util.AmazonS3Util;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import com.cook.cookapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AmazonS3Util amazonS3Util;
    private final UserRepository userRepository;
    private final ChatRoomRedisService chatRoomRedisService;
    private final SimpMessagingTemplate messagingTemplate;

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
                .maxParticipants(savedRoom.getMaxParticipants())
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
                .maxParticipants(room.getMaxParticipants())
                .isActive(room.isActive())
                .build();
    }

    @Override
    public ChatDtoRes.ChatRoomResponse joinRoom(Long userId, Long roomId) {
        // 채팅방이 존재하는지 확인
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

        // 마감된 방이면 입장 불가
        if (!room.isActive()) {
            throw new GeneralException(ErrorStatus.CHATROOM_CLOSED);
        }

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
                    .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                    .lastMessageTime(lastMessage != null ? lastMessage.getSentAt() : null)
                    .unreadCount(unreadCount)
                    .isActive(room.isActive())
                    .build());
        }

        return result;
    }

    // 채팅방 메시지 목록 조회
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
        // 참여자 Id 목록
        List<Long> roomUserIds = participantRepository.findUserIdsByRoomId(roomId);

        // 메시지마다 안읽은 인원 수 Redis에서 가져와서 응답 구성
        return messages.stream()
                .map(msg -> {
                    int unreadCount = chatRoomRedisService.getUnreadCount(msg.getId(), roomUserIds);// 안 읽은 사람 수
                    return ChatDtoRes.ChatMessageResponse.builder()
                            .messageId(msg.getId())// 메시지 ID
                            .senderId(msg.getSenderId())// 보낸 사람 ID
                            .senderNickname(msg.getSenderNickname()) // 보낸 사람 닉네임
                            .content(msg.getContent()) // 메시지 내용
                            .sentAt(msg.getSentAt()) // 보낸 시간
                            .isRead(msg.isRead() || msg.getSenderId().equals(userId)) // 내가 보낸 건 무조건 읽음 처리
                            .unreadCount(unreadCount) // 안 읽은 사람 수
                            .build();
                })
                .toList();
    }

    // 채팅방 메시지 목록 조회 (스크롤)
    @Override
    public List<ChatDtoRes.ChatMessageResponse> getMessagesByRoomIdWithScroll(Long userId, Long roomId, Long lastMessageId, int size) {
        // 채팅방 존재 여부 검증
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

        // 참여자 확인
        if (!participantRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_CHAT_ACCESS);
        }

        // 참여자 목록
        List<Long> roomUserIds = participantRepository.findUserIdsByRoomId(roomId);

        // 메시지 조회 (id 기준 역순 조회)
        Pageable pageable = PageRequest.of(0, size);
        List<ChatMessage> messages = chatMessageRepository.findMessagesBeforeId(roomId, lastMessageId, pageable);

        // id 역순으로 가져왔기 때문에 다시 정렬
        messages.sort(Comparator.comparing(ChatMessage::getId));

        return messages.stream()
                .map(msg -> {
                    int unreadCount = chatRoomRedisService.getUnreadCount(msg.getId(), roomUserIds);
                    return ChatDtoRes.ChatMessageResponse.builder()
                            .messageId(msg.getId())
                            .senderId(msg.getSenderId())
                            .senderNickname(msg.getSenderNickname())
                            .content(msg.getContent())
                            .sentAt(msg.getSentAt())
                            .isRead(msg.isRead() || msg.getSenderId().equals(userId))
                            .unreadCount(unreadCount)
                            .build();
                })
                .toList();
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

        // 채팅방 활성화 여부 확인
        if (!room.isActive()) {
            throw new GeneralException(ErrorStatus.CHATROOM_CLOSED);
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
    public List<ChatMessage> markMessagesAsRead(Long userId, Long roomId) {
        // 채팅방 존재 확인
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

        // 참여자인지 확인
        if (!participantRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_CHAT_ACCESS);
        }

        // 채팅방 활성화 여부 확인
        if (!room.isActive()) {
            throw new GeneralException(ErrorStatus.CHATROOM_CLOSED);
        }

        // 방에 있는 모든 유저 ID 목록 (unreadCount 계산용)
        List<Long> roomUserIds = participantRepository.findUserIdsByRoomId(roomId);

        List<ChatMessage> unreadMessages = chatMessageRepository
                .findByRoomIdOrderBySentAtAsc(roomId).stream()
                .filter(msg -> !msg.getSenderId().equals(userId))
                .filter(msg -> !chatRoomRedisService.hasRead(msg.getId(), userId))
                .toList();

        for (ChatMessage message : unreadMessages) {
            message.markAsRead(); // DB 업데이트
            chatRoomRedisService.markAsRead(message.getId(), userId); // Redis 기록

            //  unreadCount 계산
            int unreadCount = chatRoomRedisService.getUnreadCount(message.getId(), roomUserIds);

            //  브로드캐스트 전송
            messagingTemplate.convertAndSend(
                    "/sub/chat/room/" + roomId + "/unread",
                    ChatDtoRes.UnreadCountResponse.of(message.getId(), unreadCount)
            );
        }

        // 변경 내용 저장
        chatMessageRepository.saveAll(unreadMessages);

        return unreadMessages; // 읽은 메시지 수 반환
    }

    @Override
    public ChatDtoRes.ChatMessageResponse saveWebSocketMessage(Long roomId, Long senderId, String senderNickname, String content) {
        ChatMessage message = ChatMessage.create(roomId, senderId, senderNickname, content);
        ChatMessage saved = chatMessageRepository.save(message);

        // 총 참여자 수 - 보낸 사람 = unreadCount
        int totalParticipants = participantRepository.countByRoomId(roomId);
        int unreadCount = Math.max(totalParticipants - 1, 0); // 최소 0 보장

        // Redis에 보낸 사람은 읽은 걸로 기록
        chatRoomRedisService.markAsRead(saved.getId(), senderId);

        return ChatDtoRes.ChatMessageResponse.builder()
                .messageId(saved.getId())
                .senderId(saved.getSenderId())
                .senderNickname(saved.getSenderNickname())
                .content(saved.getContent())
                .sentAt(saved.getSentAt())
                .unreadCount(unreadCount)
                .isRead(true) // 보낸 사람은 무조건 읽음 처리
                .build();
    }

    @Override
    public void exitChatRoom(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

        ChatRoomParticipant participant = participantRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_PARTICIPANT_CHATROOM));

        boolean isHostUser = userId.equals(room.getHostUserId());

        // Redis 메시지 정리 공통 처리
        List<Long> messageIds = chatMessageRepository.findIdsByRoomId(roomId);
        for (Long mid : messageIds) {
            if (isHostUser) {
                // 방장 → Redis 키 전체 삭제
                chatRoomRedisService.deleteReadKey(mid);
            } else {
                // 일반 사용자 → 본인 read 기록만 제거
                chatRoomRedisService.removeReadUser(mid, userId);
            }
        }


        // 방장인 경우 DB에 채팅방 마감 처리
        if (isHostUser) {
            room.closeRoom(); // DB 플래그 설정
            chatRoomRepository.save(room);
        }

        // DB에서 참여자 삭제 및 인원 감소
        participantRepository.delete(participant);
        room.decreaseParticipants(); // currentParticipants 1 감소
    }

    // 채팅방 마감
    @Override
    public void closeRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

        if (!room.getHostUserId().equals(userId)) {
            throw new GeneralException(ErrorStatus.CHATROOM_NOT_OWNER);
        }

        room.closeRoom();
        chatRoomRepository.save(room);

        // Redis 키 정리
        List<Long> messageIds = chatMessageRepository.findIdsByRoomId(roomId);
        for (Long mid : messageIds) {
            chatRoomRedisService.deleteReadKey(mid);
        }
    }

    // 채팅방 엔티티 조회
    @Override
    public ChatRoom getChatRoomEntity(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));
    }

    // 채팅방 참여자 목록 조회
    @Override
    public List<ChatDtoRes.ChatRoomMemberListResponse> getRoomParticipants(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));

        List<ChatRoomParticipant> participants = participantRepository.findAllByRoomId(roomId);
        List<Long> userIds = participants.stream()
                .map(ChatRoomParticipant::getUserId)
                .toList();

        List<User> users = userRepository.findAllById(userIds);

        return users.stream()
                .map(user -> {
                    String imageUrl = amazonS3Util.getProfilePath(user.getId()); // S3 경로 가져오기
                    boolean isHost = user.getId().equals(room.getHostUserId());
                    return ChatDtoRes.ChatRoomMemberListResponse.of(user.getNickname(), imageUrl, isHost);
                })
                .toList();
    }
}