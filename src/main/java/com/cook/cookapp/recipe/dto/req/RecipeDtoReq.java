package com.cook.cookapp.recipe.dto.req;

import com.cook.cookapp.recipe.entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RecipeDtoReq {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeReq {
        private String title;
        private String instructions;
    }


}

