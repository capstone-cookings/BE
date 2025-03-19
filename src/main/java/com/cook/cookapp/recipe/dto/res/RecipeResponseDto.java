package com.cook.cookapp.recipe.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeResponseDto {
    private String title;
    private String instructions;
}

