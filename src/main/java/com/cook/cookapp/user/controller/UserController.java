package com.cook.cookapp.user.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.user.dto.req.UserDtoReq;
import com.cook.cookapp.user.dto.res.UserDtoRes;
import com.cook.cookapp.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    @PostMapping("/login")
    public ApiResponse<UserDtoRes.UserLoginRes> login(@RequestBody UserDtoReq.LoginReq loginDto, HttpServletRequest request, HttpServletResponse response) {

        return ApiResponse.onSuccess(userService.login(request,response,loginDto));
    }
}
