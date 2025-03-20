package com.cook.cookapp.recipe.service;

import com.cook.cookapp.recipe.dto.req.RecipeDtoReq;
import com.cook.cookapp.recipe.dto.res.RecipeDtoRes;
import com.cook.cookapp.recipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeService {
    Page<RecipeDtoRes.UserRecipeRes> findAll(Pageable pageable);
    Page<RecipeDtoRes.UserRecipeRes> findByUserId(Long userId, Pageable pageable);
    void addRecipe(Long userId, RecipeDtoReq.RecipeReq requestDto);
}
