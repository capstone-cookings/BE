package com.cook.cookapp.recipe.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RecipeDtoReq {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeReq {

        private String title;

        private List<String> ingredients;

        private String instructions;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreRecipeReq {

        private String recipe;
    }


}

