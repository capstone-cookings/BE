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
    @Operation(summary = "채팅방 전체 목록 조회")
    @GetMapping("/rooms")
    public ApiResponse<List<ChatDtoRes.ChatRoomResponse>> getAllRooms() {
        return ApiResponse.of(SuccessStatus._OK, chatService.getAllRooms());
    }

    @Operation(summary = "채팅방 단일 조회")
    @GetMapping("/room/{roomId}")
    public ApiResponse<ChatDtoRes.ChatRoomResponse> getRoom(@PathVariable Long roomId) {
        return ApiResponse.of(SuccessStatus._OK, chatService.getRoomById(roomId));
    }

    @Operation(summary = "채팅방 입장")
    @PostMapping("/room/{roomId}/join")
    public ApiResponse<ChatDtoRes.ChatRoomResponse> joinRoom(@PathVariable Long roomId) {
        return ApiResponse.of(SuccessStatus._OK, chatService.joinRoom(roomId));
    }
}
