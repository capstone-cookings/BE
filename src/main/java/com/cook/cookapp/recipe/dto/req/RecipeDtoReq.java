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
        //TODO 사진도 넣어야 함.

        @NotBlank(message = "레시피 이름은 필수입니다.")
        private String title;

        @NotBlank(message = "레시피 방법은 필수입니다.")
        private String instructions;
    }


}

