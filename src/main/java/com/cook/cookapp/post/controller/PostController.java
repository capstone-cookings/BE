package com.cook.cookapp.post.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.service.PostService;
import com.cook.cookapp.post.service.PostServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostServiceImpl postServiceImpl;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "게시글 등록 API", description = "사용자가 게시글을 등록합니다")
    @PostMapping
    public ApiResponse<String> addPost(@RequestBody @Valid PostDtoReq postDtoReq) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        postService.addPost(userId, postDtoReq);
        return ApiResponse.onSuccess("게시글을 등록하였습니다");
    }

}
