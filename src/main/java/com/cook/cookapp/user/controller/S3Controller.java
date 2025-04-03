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

        String imageUrl = amazonS3Util.profileImageUpload(profileImage, userId);

        return ApiResponse.onSuccess(imageUrl);
    }

    @Operation(summary = "레시피 이미지 업로드 API", description = "레시피 이미지를 업로드 합니다.")
    @PostMapping(value = "/update-recipe/{recipeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateRecipeImage(@RequestPart("recipeImage") MultipartFile recipeImage, @PathVariable Long recipeId) throws IOException {
        Long userId = jwtTokenProvider.getUserIdFromToken();

        String imageUrl = amazonS3Util.recipeImageUpload(recipeImage, recipeId, userId);

        return ApiResponse.onSuccess(imageUrl);
    }

    @Operation(summary = "게시글 이미지 업로드 API", description = "게시글 이미지를 업로드 합니다.")
    @PostMapping(value = "/update-post/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<String>> updateRecipeImage(@RequestPart("postImage") List<MultipartFile> postImages, @PathVariable Long postId) throws IOException {
        Long userId = jwtTokenProvider.getUserIdFromToken();

        List<String> imageUrls = amazonS3Util.postImageUpload(postImages, postId, userId);

        return ApiResponse.onSuccess(imageUrls);
    }
}
