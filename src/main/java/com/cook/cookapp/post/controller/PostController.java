package com.cook.cookapp.post.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.dto.res.PostResDto;
import com.cook.cookapp.post.service.PostService;
import com.cook.cookapp.post.service.PostServiceImpl;
import com.cook.cookapp.recipe.dto.res.RecipeResDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @PostMapping("")
    public ApiResponse<String> addPost(@RequestBody @Valid PostDtoReq postDtoReq) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        postService.addPost(userId, postDtoReq);
        return ApiResponse.onSuccess("게시글을 등록하였습니다");
    }


    @Operation(summary = "게시글 조회 API", description = "사용자의 게시글을 조회합니다")
    @GetMapping("")
    public ApiResponse<Page<PostResDto.UserPostRes>> getPost(
            @RequestParam(defaultValue = "1") @Positive(message = "페이지 번호는 1 이상의 값이어야 합니다.")int page, // 기본값 1로 설정
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long userId = jwtTokenProvider.getUserIdFromToken();
        Pageable adjustedPageable = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());
        return ApiResponse.onSuccess(postService.findByUserId(userId,adjustedPageable));

    }

}
