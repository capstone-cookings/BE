package com.cook.cookapp.chat.socket;

import com.cook.cookapp.chat.dto.res.ChatDtoRes;
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

    @MessageMapping("/chat/message")
    public void handleChatMessage(ChatMessageSocketRequest request, Principal principal) {
        // Principal → userId 추출
        Long userId = jwtTokenProvider.getUserIdFromPrincipal(principal);

        // 유저 정보 조회
        User user = userService.getUserById(userId);

        // 서비스 호출
        ChatDtoRes.ChatMessageResponse response = chatService.saveWebSocketMessage(
                request.getRoomId(),
                user.getId(),
                user.getNickname(),
                request.getContent()
        );

        // 메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + request.getRoomId(), response);
    }

    @MessageMapping("/chat/read")
    public void handleReadMessage(ChatReadEventRequest request, Principal principal) {
        Long userId = jwtTokenProvider.getUserIdFromPrincipal(principal);

        // 읽음 처리 (DB)
        int readCount = chatService.markMessagesAsRead(userId, request.getRoomId());

        // 응답용 메시지 생성 (예: userId + roomId + 몇 개 읽었는지)
        ChatDtoRes.ChatReadEventResponse event = ChatDtoRes.ChatReadEventResponse.builder()
                .roomId(request.getRoomId())
                .userId(userId)
                .readCount(readCount)
                .build();

        // 읽음 이벤트 broadcast
        messagingTemplate.convertAndSend("/sub/chat/room/" + request.getRoomId() + "/read", event);
    }
}
