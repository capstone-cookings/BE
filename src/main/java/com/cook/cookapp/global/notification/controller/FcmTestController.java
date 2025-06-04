package com.cook.cookapp.global.notification.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.global.notification.services.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class FcmTestController {

    private final FcmService fcmService;

    @Operation(summary = "FCM 푸시 테스트 API", description = "지정한 FCM 토큰으로 푸시 알림을 전송합니다")
    @PostMapping("/fcm")
    public ApiResponse<String> testSendFcm(@RequestParam String token,
                                           @RequestParam String title,
                                           @RequestParam String body) {
        fcmService.sendFcm(token, title, body, Long.parseLong("999999999"));
        return ApiResponse.onSuccess("FCM 테스트 전송 완료");
    }
}

