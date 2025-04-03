package com.cook.cookapp.recipe.service;

import com.cook.cookapp.recipe.dto.req.RecipeDtoReq;
import com.cook.cookapp.recipe.dto.res.RecipeDtoRes;
import com.cook.cookapp.recipe.entity.Recipe;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeService {
    Page<RecipeDtoRes.MyRecipeRes> findByUserId(Long userId, Pageable pageable);
    RecipeDtoRes.IsLikedRecipeRes likeRecipe(Long userId, Long recipeId);
    RecipeDtoRes.UserRecipeRes getRecipeById(Long userId,Long recipeId);
    void parseChatbotRecipe(Long userId,RecipeDtoReq.StoreRecipeReq request);
    void updateRecipe(Long recipeId, Long userId, RecipeDtoReq.RecipeReq requestDto);
    void deleteRecipe(Long recipeId,Long userId);
}
