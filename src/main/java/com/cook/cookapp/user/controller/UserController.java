package com.cook.cookapp.user.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.apiPayload.code.status.SuccessStatus;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.user.dto.req.KakaoAccessTokenRequest;
import com.cook.cookapp.user.dto.req.UserDtoReq;
import com.cook.cookapp.user.dto.res.KakaoUserInfoResponseDto;
import com.cook.cookapp.user.dto.res.UserDtoRes;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.service.KakaoService;
import com.cook.cookapp.user.service.UserService;
import com.cook.cookapp.user.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    private final KakaoService kakaoService;
    private final UserServiceImpl userServiceImpl;

    @Operation(summary = "앱 카카오로그인 API", description = "앱에서 카카오 로그인")
    @PostMapping("/kakao-login")
    public ApiResponse<UserDtoRes.UserLoginRes> kakaoLogin(@RequestBody @Valid KakaoAccessTokenRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(request.getAccessToken());
        User user = userService.kakaoSignup(userInfo);
        return ApiResponse.onSuccess(userService.kakaoLogin(httpRequest, httpResponse, user));
    }


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
        String accessToken = jwtTokenProvider.resolveAccessToken();

        if (refreshToken == null) {
            return ApiResponse.onFailRefresh();
        }

        Long userId = jwtTokenProvider.getUserIdInToken(refreshToken);

        // 리프레시 토큰 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken, userId)) {
            return ApiResponse.onFailRefresh();
        }

        // 기존 RefreshToken 무효화 (삭제)
        jwtTokenProvider.deleteRefreshToken(userId);

        // 기존 AccessToken 블랙리스트 처리 추가
        if (accessToken != null) {
            jwtTokenProvider.invalidateToken(accessToken);
        }

        // 새로운 액세스 토큰 & 리프레시 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        // 응답 데이터 생성
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return ApiResponse.onSuccess(tokens);
    }

    @Operation(summary = "닉네임 생성 API", description = "닉네임을 생성합니다")
    @PostMapping("/nickname")
    public ApiResponse<String> addNickname(@RequestParam String nickname) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        if (nickname == null || nickname.trim().isEmpty() || nickname.length() < 2 || nickname.length() > 20) {
            throw new GeneralException(ErrorStatus.INVALID_NICKNAME);
        }

        if (userService.duplicateNickname(nickname)) {
            throw new GeneralException(ErrorStatus.NICKNAME_DUPLICATION);
        }
        userService.addNickname(userId, nickname);
        return ApiResponse.onSuccess("닉네임 생성 완료");
    }

    @Operation(summary = "로그인된 사용자 프로필 조회 API", description = "현재 로그인된 사용자의 프로필을 조회합니다.")
    @GetMapping("/profile")
    public ApiResponse<UserDtoRes.UserProfileRes> getProfile() {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(userServiceImpl.getUserProfileById(userId));
    }

    @Operation(summary = "다른 사용자 프로필 조회 API", description = "다른 사용자의 프로필을 조회합니다.")
    @GetMapping("/profile/other")
    public ApiResponse<UserDtoRes.UserProfileRes> getOtherProfile(@RequestParam String nickname) {
        User user = userServiceImpl.getUserByNickname(nickname);
        return ApiResponse.onSuccess(userServiceImpl.getUserProfileByNickname(user.getNickname()));
    }

//    // 공동구매 커뮤니티 매너 평가로 변경해야 함
//    @Operation(summary = "사용자 경험치 변경 API", description = "매너평가로 사용자 경험치 변경합니다")
//    @PatchMapping("/exp")
//    public ApiResponse<UserProfileResponse> chagneUserExp(@RequestParam Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        return ApiResponse.onSuccess(new UserProfileResponse(user));
//    }

    @Operation(summary = "음식 취향을 입력/수정하는 API", description = "입력값 검증 후 저장")
    @PostMapping("/taste-preference")
    public ApiResponse<String> updateTastePreference(@RequestBody UserDtoReq.TastePreferenceRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        userService.updateTastePreference(userId, request);
        return ApiResponse.onSuccess("취향이 성공적으로 업데이트되었습니다.");
    }

    @Operation(summary = "음식 취향 조회 API", description = "저장된 음식 취향 값을 반환")
    @GetMapping("/taste-preference")
    public ApiResponse<UserDtoRes.TastePreferenceRes> getTastePreference() {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        String tastePreference = userService.getTastePreference(userId);
        return ApiResponse.onSuccess(new UserDtoRes.TastePreferenceRes(tastePreference));
    }

}
