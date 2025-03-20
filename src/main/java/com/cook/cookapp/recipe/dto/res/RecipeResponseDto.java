package com.cook.cookapp.recipe.dto.res;

import com.cook.cookapp.recipe.entity.Recipe;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeResponseDto {
    private String title;
    private String instructions;
    public static RecipeResponseDto fromEntity(Recipe recipe) {
        return new RecipeResponseDto(recipe.getTitle(), recipe.getInstructions());
    }
}

