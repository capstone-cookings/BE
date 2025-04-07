package com.cook.cookapp.post.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.dto.res.SearchResDto;
import com.cook.cookapp.post.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
public class SearchHistoryController {
    private final SearchHistoryService searchHistoryService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "검색 기록 조회 API", description = "사용자가 검색한 기록을 조회합니다")
    @GetMapping("")
    public ApiResponse<List<SearchResDto.searchedRes>> getSearchHistory() {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(searchHistoryService.getSearchHistory(userId));
    }

    @Operation(summary = "검색 기록 삭제 API", description = "사용자가 검색한 기록을 삭제합니다")
    @DeleteMapping("")
    public ApiResponse<String> deleteSearchHistory(@RequestParam String keyword) {
        //@Pathvariable은 공백 및 특수문자 반영 x 인코딩 할 필요 없는 RequestParam으로 하기
        Long userId = jwtTokenProvider.getUserIdFromToken();
        searchHistoryService.deleteSearchHistory(userId, keyword);
        return ApiResponse.onSuccess("검색 기록 삭제 완료하였습니다");
    }

    @Operation(summary = "검색 기록 전체 삭제 API", description = "사용자가 검색한 기록 모두를 삭제합니다")
    @DeleteMapping("/all")
    public ApiResponse<String> deleteAllSearchHistory() {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        searchHistoryService.deleteAllSearchHistory(userId);
        return ApiResponse.onSuccess("검색 기록 전체 삭제 완료하였습니다");
    }
}
