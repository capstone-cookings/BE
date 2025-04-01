package com.cook.cookapp.user.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.global.util.AmazonS3Util;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
}
