package com.cook.cookapp.recipe.controller;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.recipe.dto.res.RecipeResponseDto;
import com.cook.cookapp.recipe.service.RecipeService;
import com.cook.cookapp.user.dto.req.UserDtoReq;
import com.cook.cookapp.user.service.KakaoService;
import com.cook.cookapp.user.service.UserService;
import com.cook.cookapp.user.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

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
            Pageable pageable) {

        Long userId = jwtTokenProvider.getUserIdFromToken();
        Page<RecipeResponseDto> myRecipes = recipeService.findByUserId(userId, pageable);

        return ResponseEntity.ok(ApiResponse.onSuccess(myRecipes));
    }
}
