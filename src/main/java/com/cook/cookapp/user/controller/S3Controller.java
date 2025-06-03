package com.cook.cookapp.user.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.global.util.AmazonS3Util;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final AmazonS3Util amazonS3Util;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "프로필 이미지 업로드 API", description = "프로필 이미지를 업로드 합니다.")
    @PostMapping(value = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateProfileImage(@RequestPart("profileImage") MultipartFile profileImage) throws IOException {
        Long userId = jwtTokenProvider.getUserIdFromToken();

        amazonS3Util.profileImageUpload(profileImage, userId);

        return ApiResponse.onSuccess("프로필 이미지 업로드 성공했습니다");
    }

    @Operation(summary = "프로필 이미지 삭제 API", description = "프로필 이미지를 삭제합니다.")
    @DeleteMapping("/delete-profile")
    public ApiResponse<String> deleteProfileImage() {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        amazonS3Util.deleteProfileImage(userId);
        return ApiResponse.onSuccess("게시글 이미지 삭제에 성공했습니다");
    }

    @Operation(summary = "레시피 이미지 업로드 API", description = "레시피 이미지를 업로드 합니다.")
    @PostMapping(value = "/update-recipe/{recipeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateRecipeImage(@RequestPart("recipeImage") MultipartFile recipeImage, @PathVariable Long recipeId) throws IOException {
        Long userId = jwtTokenProvider.getUserIdFromToken();

        amazonS3Util.recipeImageUpload(recipeImage, recipeId, userId);

        return ApiResponse.onSuccess("이미지 업로드 성공했습니다");
    }

    //게시글 이미지 추가 (개별 추가)
    @Operation(summary = "게시글 이미지 추가 API", description = "게시글에 이미지를 추가합니다.")
    @PostMapping(value = "/add-post-image/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> addPostImage(@RequestPart("postImage") List<MultipartFile> postImages, @PathVariable Long postId) throws IOException {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        amazonS3Util.addPostImage(postImages, postId, userId);
        return ApiResponse.onSuccess("게시글 이미지 추가에 성공했습니다");
    }

    //게시글 이미지 삭제 (개별 삭제)
    @Operation(summary = "게시글 이미지 삭제 API", description = "게시글의 특정 이미지를 삭제합니다.")
    @DeleteMapping("/delete-post-image/{postId}/{imageId}")
    public ApiResponse<String> deletePostImage(@PathVariable Long postId, @PathVariable Long imageId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        amazonS3Util.deletePostImage(postId, imageId, userId);
        return ApiResponse.onSuccess("게시글 이미지 삭제에 성공했습니다");
    }
}
