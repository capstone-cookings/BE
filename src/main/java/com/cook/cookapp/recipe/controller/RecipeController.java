package com.cook.cookapp.recipe.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.recipe.dto.res.RecipeResponseDto;
import com.cook.cookapp.recipe.service.RecipeService;
import com.cook.cookapp.user.dto.req.UserDtoReq;
import com.cook.cookapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeController {
    private final UserService userService;
    private final RecipeService recipeService;
    private final JwtTokenProvider jwtTokenProvider;


    @Operation(summary = "레시피 저장 API", description = "My 레시피에 레시피를 저장합니다")
    @PostMapping("")
    public ApiResponse<RecipeResponseDto> storeRecipe(
            @RequestBody @Valid UserDtoReq.RecipeReq requestDto) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        RecipeResponseDto responseDto = userService.storeRecipe(userId, requestDto);
        return ApiResponse.onSuccess(responseDto);
    }

    @Operation(summary = "레시피 조회 API", description = "My 레시피를 조회합니다")
    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<RecipeResponseDto>>> getRecipe(
            @RequestParam(defaultValue = "1") @Positive(message = "페이지 번호는 1 이상의 값이어야 합니다.") int page, // 기본값 1로 설정
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long userId = jwtTokenProvider.getUserIdFromToken();

        // 1-based index를 0-based로 변환
        Pageable adjustedPageable = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        Page<RecipeResponseDto> myRecipes = recipeService.findByUserId(userId, adjustedPageable);

        return ResponseEntity.ok(ApiResponse.onSuccess(myRecipes));
    }

}
