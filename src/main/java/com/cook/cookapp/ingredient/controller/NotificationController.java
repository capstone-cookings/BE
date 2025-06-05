package com.cook.cookapp.ingredient.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.ingredient.dto.res.IngredientNotificationDtoRes;
import com.cook.cookapp.ingredient.service.IngredientNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final IngredientNotificationService ingredientNotificationService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "소비기한 알림 목록 조회 API", description = "소비기한 임박 알림 목록을 조회합니다.")
    @GetMapping("")
    public ApiResponse<List<IngredientNotificationDtoRes>> getMyNotifications() {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(ingredientNotificationService.getMyNotifications(userId));
    }

    @Operation(summary = "해당 알림 읽음 처리 API", description = "입력한 id의 알알림을 읽음 처리 합니다.")
    @PostMapping("/{id}/read")
    public ApiResponse<String> markAsRead(@PathVariable Long id) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        ingredientNotificationService.markAsRead(userId, id);
        return ApiResponse.onSuccess("알림을 확인했습니다.");
    }

    @Operation(summary = "안읽은 알림수 조회 API", description = "사용자가 확인하지 않은 알림 수를 조회합니다.")
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(ingredientNotificationService.countUnread(userId));
    }
}

