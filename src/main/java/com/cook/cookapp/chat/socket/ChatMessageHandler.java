package com.cook.cookapp.chat.socket;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.chat.dto.res.ChatDtoRes;
import com.cook.cookapp.chat.entity.ChatMessage;
import com.cook.cookapp.chat.entity.ChatRoom;
import com.cook.cookapp.chat.redis.ChatRoomRedisService;
import com.cook.cookapp.chat.repository.ChatRoomParticipantRepository;
import com.cook.cookapp.chat.service.ChatService;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

/**
 * WebSocket 메시지 핸들러
 * - STOMP 프로토콜로 메시지 수신 및 브로드캐스트 처리
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ChatRoomRedisService chatRoomRedisService;
    private final ChatRoomParticipantRepository participantRepository;

    @MessageMapping("/chat/read")
    public void readMessages(ChatReadEventRequest request, Principal principal) {
        Long userId = jwtTokenProvider.getUserIdFromPrincipal(principal);
        Long roomId = request.getRoomId();

        // 읽음 처리: DB 업데이트 + Redis 기록
        List<ChatMessage> unreadMessages = chatService.markMessagesAsRead(userId, roomId);

        // 채팅방 참여자 수 (전체 인원)
        int totalParticipants = participantRepository.countByRoomId(roomId);

        for (ChatMessage message : unreadMessages) {
            Long messageId = message.getId();

            // Redis 기준 읽은 사람 수
            int readCount = chatRoomRedisService.getReadCount(messageId);
            int unreadCount = totalParticipants - readCount;

            // 내가 보낸 메시지는 unreadCount = 0 처리
            if (message.getSenderId().equals(userId)) {
                unreadCount = 0;
            }

            ChatDtoRes.ChatUnreadBroadcast payload = ChatDtoRes.ChatUnreadBroadcast.builder()
                    .messageId(messageId)
                    .unreadCount(unreadCount)
                    .build();

            //  /sub/chat/room/{roomId}/unread 구독자에게 전송
            messagingTemplate.convertAndSend(
                    "/sub/chat/room/" + roomId + "/unread",
                    payload
            );
        }
    }
    @MessageMapping("/chat/message")
    public void handleChatMessage(ChatMessageSocketRequest request, Principal principal) {
        Long userId = jwtTokenProvider.getUserIdFromPrincipal(principal);
        User user = userService.getUserById(userId);

        // 마감된 방인지 체크
        ChatRoom room = chatService.getChatRoomEntity(request.getRoomId());
        if (!room.isActive()) {
            throw new GeneralException(ErrorStatus.CHATROOM_CLOSED);
        }

        // 메시지 저장 및 응답 전송
        ChatDtoRes.ChatMessageResponse response = chatService.saveWebSocketMessage(
                request.getRoomId(),
                user.getId(),
                user.getNickname(),
                request.getContent()
        );
        // WebSocket 구독자에게 메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + request.getRoomId(), response);
    }

}

