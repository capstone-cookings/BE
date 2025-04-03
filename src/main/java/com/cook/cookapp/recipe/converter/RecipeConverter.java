package com.cook.cookapp.recipe.converter;


import com.cook.cookapp.ingredient.dto.req.IngredientDtoReq;
import com.cook.cookapp.ingredient.dto.res.IngredientDtoRes;
import com.cook.cookapp.ingredient.entity.Enum.AlarmStatus;
import com.cook.cookapp.ingredient.entity.Enum.StorageType;
import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.recipe.dto.req.RecipeDtoReq;
import com.cook.cookapp.recipe.dto.res.RecipeDtoRes;
import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class RecipeConverter {
    public Recipe toEntity(RecipeDtoReq.RecipeReq dto, User user) {
        return Recipe.builder()
                .title(dto.getTitle())
                .instructions(dto.getInstructions())
                .user(user)
                .build();

    }

    public RecipeDtoRes.MyRecipeRes toDto(Recipe recipe) {
        return RecipeDtoRes.MyRecipeRes.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .isLiked(recipe.isLiked())
                .build();
    }
}
