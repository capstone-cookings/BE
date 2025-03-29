package com.cook.cookapp.chat.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.apiPayload.code.status.SuccessStatus;
import com.cook.cookapp.chat.dto.req.ChatDtoReq;
import com.cook.cookapp.chat.dto.res.ChatDtoRes;
import com.cook.cookapp.chat.service.ChatService;
import com.cook.cookapp.common.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "채팅방 생성 API", description = "사용자가 새로운 채팅방을 생성합니다.")
    @PostMapping("/room")
    public ApiResponse<ChatDtoRes.ChatRoomResponse> createRoom(@RequestBody ChatDtoReq.ChatRoomCreateRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        ChatDtoRes.ChatRoomResponse response = chatService.createChatRoom(userId, request);
        return ApiResponse.of(SuccessStatus._OK, response);
    }
    @Operation(summary = "참여중인 채팅방 목록 조회 API",description = "사용자가 참여중인 채팅방 목록을 조회합니다.")
    @GetMapping("/my-rooms")
    public ApiResponse<List<ChatDtoRes.ChatRoomListItemResponse>> getMyRooms() {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.of(SuccessStatus._OK, chatService.getMyChatRooms(userId));
    }

    @Operation(summary = "채팅방 단일 조회 API", description = "채팅방의 상세 정보를 조회합니다.")
    @GetMapping("/room/{roomId}")
    public ApiResponse<ChatDtoRes.ChatRoomResponse> getRoom(@PathVariable Long roomId) {
        return ApiResponse.of(SuccessStatus._OK, chatService.getRoomById(roomId));
    }

    @Operation(summary = "채팅방 입장 API", description = "사용자가 채팅방에 입장합니다.")
    @PostMapping("/room/{roomId}/join")
    public ApiResponse<ChatDtoRes.ChatRoomResponse> joinRoom(@PathVariable Long roomId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.of(SuccessStatus._OK, chatService.joinRoom(userId, roomId));
    }

    @Operation(summary = "채팅 메시지 전송 API", description = "채팅방에 메시지를 저장합니다.")
    @PostMapping("/room/{roomId}/message")
    public ApiResponse<ChatDtoRes.ChatMessageResponse> sendMessage(
            @PathVariable Long roomId,
            @RequestBody ChatDtoReq.ChatMessageRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.of(SuccessStatus._OK, chatService.sendMessage(userId, roomId, request));
    }

    @Operation(summary = "채팅방 입장시 메시지 읽음 처리 API", description = "채팅방에 입장하면 메시지를 읽음 처리합니다.")
    @PostMapping("/room/{roomId}/read")
    public ApiResponse<Integer> markMessagesAsRead(@PathVariable Long roomId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        int readCount = chatService.markMessagesAsRead(userId, roomId).size();;
        return ApiResponse.of(SuccessStatus._OK, readCount);
    }

    @Operation(summary = "채팅방 나가기 API", description = "사용자가 채팅방에서 나갑니다.")
    @DeleteMapping("/room/{roomId}/exit")
    public ApiResponse<String> exitRoom(@PathVariable Long roomId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        chatService.exitChatRoom(userId, roomId);
        return ApiResponse.of(SuccessStatus._OK, "채팅방에서 정상적으로 나갔습니다.");
    }

    @Operation(summary = "채팅방 마감")
    @PostMapping("/room/{roomId}/close")
    public ApiResponse<String> closeRoom(@PathVariable Long roomId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        chatService.closeRoom(roomId, userId);
        return ApiResponse.of(SuccessStatus._OK, "채팅방이 마감되었습니다.");
    }
}
