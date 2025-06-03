package com.cook.cookapp.ingredient.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.ingredient.dto.req.IngredientDtoReq;
import com.cook.cookapp.ingredient.dto.req.IngredientUpdateDtoReq;
import com.cook.cookapp.ingredient.dto.res.IngredientDtoRes;
import com.cook.cookapp.ingredient.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/ingredient")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "식재료 등록 API", description = "사용자가 식재료를 저장소에 추가합니다.")
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> addIngredient(@RequestPart @Valid IngredientDtoReq ingredientDtoReq,
                                             @RequestPart(value = "ingredientImage", required = false) MultipartFile ingredientImage) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        ingredientService.addIngredient(userId, ingredientDtoReq,ingredientImage);
        return ApiResponse.onSuccess("식재료가 추가되었습니다.");
    }

    @Operation(summary = "식재료 전체 조회 (등록 순) API", description = "사용자가 저장한 모든 식재료를 등록 순으로 조회합니다.")
    @GetMapping("/createdAt")
    public ApiResponse<Page<IngredientDtoRes>> getAllIngredientsByCreatedAt(
            @RequestParam(defaultValue = "1") @Positive(message = "페이지 번호는 1 이상의 값이어야 합니다.") int page, @PageableDefault(size = 10) Pageable pageable) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        Pageable adjustedPageable = PageRequest.of(page - 1, pageable.getPageSize());
        return ApiResponse.onSuccess(ingredientService.getAllIngredientsByCreatedAt(userId, adjustedPageable));
    }

    @Operation(summary = "식재료 전체 조회 (소비기한 순) API", description = "사용자가 저장한 모든 식재료를 소비기한이 빠른 순으로 조회합니다.")
    @GetMapping("/useByDate")
    public ApiResponse<Page<IngredientDtoRes>> getAllIngredientsByUseByDate(
            @RequestParam(defaultValue = "1") @Positive(message = "페이지 번호는 1 이상의 값이어야 합니다.") int page,@PageableDefault(size = 10) Pageable pageable) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        Pageable adjustedPageable = PageRequest.of(page - 1, pageable.getPageSize());
        return ApiResponse.onSuccess(ingredientService.getAllIngredientsByUseByDate(userId, adjustedPageable));
    }

    @Operation(summary = "식재료 삭제 API", description = "사용자가 저장한 특정 식재료를 삭제합니다.")
    @DeleteMapping("/{ingredientId}")
    public ApiResponse<String> deleteIngredient(@PathVariable Long ingredientId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        ingredientService.deleteIngredient(userId, ingredientId);
        return ApiResponse.onSuccess("식재료가 삭제되었습니다.");
    }

    @Operation(summary = "식재료 수정 API", description = "사용자가 저장한 특정 식재료의 정보를 수정합니다.")
    @PatchMapping(value = "/{ingredientId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateIngredient(@PathVariable Long ingredientId,
                                                @RequestPart @Valid IngredientUpdateDtoReq ingredientDtoReq,
                                                @RequestPart(value = "ingredientImage", required = false) MultipartFile ingredientImage) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        ingredientService.updateIngredient(userId, ingredientId, ingredientDtoReq,ingredientImage);
        return ApiResponse.onSuccess("식재료 정보가 수정되었습니다.");
    }

    @Operation(summary = "유통기한 알림 설정 API", description = "사용자가 특정 식재료의 알림 설정을 ON/OFF 합니다.")
    @PatchMapping("/{ingredientId}/alarm")
    public ApiResponse<String> updateAlarmStatus(@PathVariable Long ingredientId, @RequestParam boolean alarmStatus) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        ingredientService.updateAlarmStatus(userId, ingredientId, alarmStatus);
        return ApiResponse.onSuccess("알림 설정이 변경되었습니다.");
    }

    @Operation(summary = "특정 식재료 정보 조회 API", description = "식재료 ID를 이용해 특정 식재료의 정보를 조회합니다.")
    @GetMapping("/{ingredientId}")
    public ApiResponse<IngredientDtoRes> getIngredientById(@PathVariable Long ingredientId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(ingredientService.getIngredientById(userId, ingredientId));
    }
}
