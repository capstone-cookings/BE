package com.cook.cookapp.chat.socket;

import com.cook.cookapp.chat.dto.res.ChatDtoRes;
import com.cook.cookapp.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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

    @MessageMapping("/chat/message") // /pub/chat/message
    public void handleChatMessage(ChatMessageSocketRequest request) {
        log.info("[WebSocket 수신] room: {}, sender: {}, content: {}", request.getRoomId(), request.getSenderId(), request.getContent());

        // 메시지 저장
        ChatDtoRes.ChatMessageResponse saved = chatService.saveWebSocketMessage(request);

        // 메시지 전송 (해당 채팅방 구독자에게)
        messagingTemplate.convertAndSend("/sub/chat/room/" + request.getRoomId(), saved);
    }
}
