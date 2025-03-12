package com.cook.cookapp.user.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.apiPayload.code.status.SuccessStatus;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.user.dto.req.UserDtoReq;
import com.cook.cookapp.user.dto.res.UserDtoRes;
import com.cook.cookapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    @Operation(summary = "로그인 API", description = "로그인")
    @PostMapping("/login")
    public ApiResponse<UserDtoRes.UserLoginRes> login(@RequestBody UserDtoReq.LoginReq loginDto, HttpServletRequest request, HttpServletResponse response) {

        return ApiResponse.onSuccess(userService.login(request,response,loginDto));
    }
    @Operation(summary = "로그아웃 API", description = "액세스 토큰을 무효화하여 로그아웃")
    @PostMapping("/logout")
    public ApiResponse<SuccessStatus> logout(
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            HttpServletRequest request, HttpServletResponse response) {

        userService.logout(request, response, accessToken);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @Operation(summary = "토큰 재발급 API", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰 발급")
    @PostMapping("/refresh")
    public ApiResponse<Map<String, String>> refresh(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken();

        if (refreshToken == null) {
            return ApiResponse.onFailure("TOKEN_REQUIRED", "리프레시 토큰이 필요합니다.", null);
        }

        Long userId = jwtTokenProvider.getUserIdInToken(refreshToken);

        // 리프레시 토큰 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken, userId)) {
            return ApiResponse.onFailure("INVALID_TOKEN", "리프레시 토큰이 유효하지 않습니다. 다시 로그인하세요.", null);
        }

        // 기존 RefreshToken 무효화 (삭제)
        jwtTokenProvider.deleteRefreshToken(userId);

        // 새로운 액세스 토큰 & 리프레시 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        // 응답 데이터 생성
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return ApiResponse.onSuccess(tokens);
    }
}
