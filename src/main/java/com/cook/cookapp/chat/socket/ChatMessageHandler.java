package com.cook.cookapp.chat.socket;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.chat.dto.res.ChatDtoRes;
import com.cook.cookapp.chat.entity.ChatMessage;
import com.cook.cookapp.chat.entity.ChatRoom;
import com.cook.cookapp.chat.redis.ChatRoomRedisService;
import com.cook.cookapp.chat.redis.RedisWebSocketSessionManager;
import com.cook.cookapp.chat.repository.ChatRoomParticipantRepository;
import com.cook.cookapp.chat.service.ChatService;
import com.cook.cookapp.chat.socket.dto.req.ChatMessageSocketRequest;
import com.cook.cookapp.chat.socket.dto.req.ChatReadEventRequest;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.global.notification.services.FcmService;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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
    private final RedisWebSocketSessionManager sessionManager;
    private final FcmService fcmService;

    @MessageMapping("/chat/enter")
    public void enter(ChatReadEventRequest request, Principal principal,  StompHeaderAccessor accessor) {
        Long userId = jwtTokenProvider.getUserIdFromPrincipal(principal);
        Long roomId = request.getRoomId();

        String sessionId = accessor.getSessionId();
        sessionManager.register(sessionId, userId, roomId);

        if (!participantRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_CHAT_ACCESS);
        }

        chatRoomRedisService.addConnectedUser(roomId, userId);
        log.info("WebSocket 입장: 유저 {} → 방 {}", userId, roomId);
    }

    @MessageMapping("/chat/leave")
    public void leave(ChatReadEventRequest request, Principal principal) {
        Long userId = jwtTokenProvider.getUserIdFromPrincipal(principal);
        Long roomId = request.getRoomId();

        if (!participantRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_CHAT_ACCESS);
        }

        chatRoomRedisService.removeConnectedUser(roomId, userId);
        log.info("WebSocket 퇴장: 유저 {} → 방 {}", userId, roomId);
    }


    @MessageMapping("/chat/read")
    public void readMessages(ChatReadEventRequest request, Principal principal) {
        Long userId = jwtTokenProvider.getUserIdFromPrincipal(principal);
        Long roomId = request.getRoomId();

        // 읽음 처리: DB 업데이트 + Redis 기록
        List<ChatMessage> unreadMessages = chatService.markMessagesAsRead(userId, roomId);


        // 현재 참여자 ID 목록
        List<Long> roomUserIds = participantRepository.findUserIdsByRoomId(roomId);

        for (ChatMessage message : unreadMessages) {
            Long messageId = message.getId();

            // Redis 기준 unread 계산
            int unreadCount = chatRoomRedisService.getUnreadCount(messageId, roomUserIds);

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
        User sender = userService.getUserById(userId);
        Long roomId = request.getRoomId();

        // 참여자 권한 확인
        if (!participantRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_CHAT_ACCESS);
        }

        // 마감된 방인지 체크
        ChatRoom room = chatService.getChatRoomEntity(roomId);
        if (!room.isActive()) {
            throw new GeneralException(ErrorStatus.CHATROOM_CLOSED);
        }

        // 메시지 저장
        ChatDtoRes.ChatMessageResponse response = chatService.saveWebSocketMessage(
                roomId,
                sender.getId(),
                sender.getNickname(),
                request.getContent()
        );

        // 메시지 브로드캐스트
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, response);

        //  FCM 푸시 전송 (연결 안된 사용자에게만)
        List<Long> connectedUserIds = chatRoomRedisService.getConnectedUserIds(roomId);
        List<Long> allUserIds = participantRepository.findUserIdsByRoomId(roomId);

        for (Long targetUserId : allUserIds) {
            if (targetUserId.equals(userId)) continue; // 본인은 제외
            if (!connectedUserIds.contains(targetUserId)) {
                User targetUser = userService.getUserById(targetUserId);
                String token = targetUser.getFcmToken();
                if (token != null && !token.isBlank()) {
                    fcmService.sendFcm(
                            token,
                            sender.getNickname() + "님의 새 메시지",
                            request.getContent()
                    );
                }
            }
        }
    }


}

