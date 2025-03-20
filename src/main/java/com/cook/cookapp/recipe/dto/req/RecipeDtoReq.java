package com.cook.cookapp.recipe.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RecipeDtoReq {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeReq {
        @NotBlank(message = "레시피 이름은 필수입니다.")
        private String title;
        private String instructions;
    }


}

