package com.cook.cookapp.post.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.dto.res.PostResDto;
import com.cook.cookapp.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "게시글 등록(채팅방 생성) API", description = "사용자가 게시글과 이미지를 함께 등록합니다.(채팅방을 자동 생성합니다.)")
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> addPost(
            @RequestPart("post") @Valid PostDtoReq postDtoReq,
            @RequestPart(value = "postImages", required = false) List<MultipartFile> postImages) throws IOException {

        Long userId = jwtTokenProvider.getUserIdFromToken();
        postService.addPost(userId, postDtoReq, postImages);
        return ApiResponse.onSuccess("게시글(채팅방) 생성완료");
    }


    @Operation(summary = "내가 쓴 글 조회 API", description = "사용자의 게시글을 조회합니다")
    @GetMapping("")
    public ApiResponse<Page<PostResDto.UserPostRes>> getPost(
            @RequestParam(defaultValue = "1") @Positive(message = "페이지 번호는 1 이상의 값이어야 합니다.")int page, // 기본값 1로 설정
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long userId = jwtTokenProvider.getUserIdFromToken();
        Pageable adjustedPageable = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());
        return ApiResponse.onSuccess(postService.findByUserId(userId,adjustedPageable));

    }

    @Operation(summary = "특정 글 조회 API", description = "특정 게시글을 조회합니다")
    @GetMapping("/{postId}")
    public ApiResponse<PostResDto.SpecPostRes> getPost(@PathVariable Long postId) {

        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(postService.getPostById(postId,userId));

    }

    @Operation(summary = "게시글 수정 API", description = "게시글을 수정합니다")
    @PatchMapping("/{postId}")
    public ApiResponse<String> modifyPost(@PathVariable Long postId,@RequestBody @Valid PostDtoReq postDtoReq) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        postService.updatePost(postId,userId,postDtoReq);
        return ApiResponse.onSuccess("게시글 수정완료하였습니다");
    }

    @Operation(summary = "게시글 삭제 API", description = "게시글을 삭제합니다")
    @DeleteMapping("/{postId}")
    public ApiResponse<String> deletePost(@PathVariable Long postId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        postService.deletePost(postId,userId);
        return ApiResponse.onSuccess("게시글 삭제 완료하였습니다");
    }

    @Operation(summary = "게시글 검색 API", description = "게시글 제목을 기준으로 검색합니다")
    @GetMapping("/search")
    public ApiResponse<Page<PostResDto.UserPostRes>> searchPosts(
            @RequestParam(required = false) String keyword, // 검색 키워드
            @RequestParam(defaultValue = "1") @Positive(message = "페이지 번호는 1 이상의 값이어야 합니다.") int page, // 기본값 1로 설정
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long userId = jwtTokenProvider.getUserIdFromToken();
        // 페이지 번호를 1-based에서 0-based로 변환
        Pageable adjustedPageable = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        return ApiResponse.onSuccess(postService.searchPosts(userId, keyword, adjustedPageable));

    }


}
