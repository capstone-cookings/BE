package com.cook.cookapp.user.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.user.dto.req.ReportDtoReq;
import com.cook.cookapp.user.service.ReportService;
import com.cook.cookapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {
    private final JwtTokenProvider jwtTokenProvider;
    private final ReportService reportService;

    @Operation(summary = "사용자 프로필 신고 API", description = "다른 사용자를 신고합니다")
    @PostMapping("/user/{targetUserId}")
    public ApiResponse<String> reportUser(
            @PathVariable Long targetUserId,
            @RequestBody ReportDtoReq.ReportRequestDto requestDto) {

        Long reporterId = jwtTokenProvider.getUserIdFromToken();
        reportService.reportUser(reporterId, targetUserId, requestDto);
        return ApiResponse.onSuccess("신고가 접수되었습니다");
    }

    @Operation(summary = "게시글에서 사용자 신고 API", description = "다른 사용자를 신고합니다")
    @PostMapping("/post/{postId}")
    public ApiResponse<String> reportUserByPost(
            @PathVariable Long postId,
            @RequestBody ReportDtoReq.ReportRequestDto requestDto) {

        Long reporterId = jwtTokenProvider.getUserIdFromToken();
        reportService.reportUserByPost(reporterId, postId, requestDto);
        return ApiResponse.onSuccess("신고가 접수되었습니다");
    }

}
