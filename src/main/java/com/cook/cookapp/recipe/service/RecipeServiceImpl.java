package com.cook.cookapp.recipe.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.recipe.converter.RecipeConverter;
import com.cook.cookapp.recipe.dto.req.RecipeDtoReq;
import com.cook.cookapp.recipe.dto.res.RecipeDtoRes;
import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.recipe.entity.RecipeIngredient;
import com.cook.cookapp.recipe.repository.RecipeRepository;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeConverter recipeConverter;

    @Override
    public Page<RecipeDtoRes.UserRecipeRes> findAll(Pageable pageable) {
        return recipeRepository.findAll(pageable).map(recipeConverter::toDto);
    }

    @Override
    public Page<RecipeDtoRes.UserRecipeRes> findByUserId(Long userId, Pageable pageable) {
        return recipeRepository.findByUserId(userId, pageable)
                .map(recipeConverter::toDto); // ✅ 내 레시피만 조회
    }

    public void addRecipe(Long userId, RecipeDtoReq.RecipeReq requestDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Recipe recipe = recipeConverter.toEntity(requestDto, user);

        List<RecipeIngredient> recipeIngredients = user.getIngredientList().stream()
                .map(ingredient -> RecipeIngredient.builder()
                        .recipe(recipe)
                        .ingredient(ingredient)
                        .build())
                .collect(Collectors.toList());

        recipe.setRecipeIngredients(recipeIngredients);
        recipeRepository.save(recipe);
    }

    public boolean likeRecipe(Long userId, Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RECIPE_NOT_FOUND));

        // 본인이 등록한 레시피인지 확인
        if (!recipe.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        // 좋아요 상태 토글
        recipe.setLiked(!recipe.isLiked());
        return recipe.isLiked();
    }

    public RecipeDtoRes.UserRecipeRes getRecipeById(Long userId,Long recipeId){
        Recipe recipe = recipeRepository.findByIdAndUserId(userId, recipeId).orElseThrow(() -> new GeneralException(ErrorStatus.RECIPE_NOT_FOUND));
        return recipeConverter.toDto(recipe);
    }


}
