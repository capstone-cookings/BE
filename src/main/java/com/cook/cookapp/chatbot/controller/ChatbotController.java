package com.cook.cookapp.chatbot.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.chatbot.dto.ChatbotResponse;
import com.cook.cookapp.chatbot.service.ChatbotService;
import com.cook.cookapp.common.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * [GET] 처음 '레시피 추천 받기' 버튼 클릭 시 호출
     * JWT 토큰에서 userId 추출 후 추천 실행
     */
    @Operation(summary = "처음 시작 레시피 추천 받기 API", description = "제일 처음 레시피 추천 받기")
    @GetMapping("/recommend")
    public ApiResponse<ChatbotResponse> recommendRecipe() {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        ChatbotResponse response = chatbotService.recommendRecipe(userId);
        return ApiResponse.onSuccess(response);
    }

    /**
     * [GET] '다른 레시피 추천 받기' 버튼 클릭 시 호출
     * 이전 추천을 제외하고 새로운 레시피 추천
     */
    @Operation(summary = "다른 레시피 추천 받기 API", description = "처음 레시피 추천 받기 API 시작 후 나온 음식 추천 x")
    @GetMapping("/recommend/another")
    public ApiResponse<ChatbotResponse> recommendAnotherRecipe() {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        ChatbotResponse response = chatbotService.recommendAnotherRecipe(userId);
        return ApiResponse.onSuccess(response);
    }
}