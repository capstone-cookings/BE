package com.cook.cookapp.user.service;

import com.cook.cookapp.user.dto.req.UserDtoReq;
import com.cook.cookapp.user.dto.res.KakaoUserInfoResponseDto;
import com.cook.cookapp.user.dto.res.UserDtoRes;
import com.cook.cookapp.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    UserDtoRes.UserLoginRes login(HttpServletRequest request, HttpServletResponse response, UserDtoReq.LoginReq loginDto);
    void logout(HttpServletRequest request, HttpServletResponse response, String accessToken);
    User kakaoSignup(KakaoUserInfoResponseDto userInfo);
    UserDtoRes.UserLoginRes kakaoLogin(HttpServletRequest request, HttpServletResponse response, User user);
    boolean duplicateNickname(String nickname);
    User getUserByNickname(String nickname);
    UserDtoRes.UserProfileRes getUserProfileById(Long userId);
    UserDtoRes.UserProfileRes getUserProfileByNickname(String nickname);
    void updateTastePreference(Long userId, UserDtoReq.TastePreferenceRequest request);
    String getTastePreference(Long userId);
    void addNickname(Long userId, String nickname);
    void addLocation(Long userId, UserDtoReq.UserLocationReq request);
    UserDtoRes.UserLocationRes getLocation(Long userId);
    void updateNickname(Long userId, String nickname);
    User getUserById(Long id);
}
