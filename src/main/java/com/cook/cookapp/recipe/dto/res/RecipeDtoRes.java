package com.cook.cookapp.recipe.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class RecipeDtoRes {

    @Data
    @AllArgsConstructor
    @Builder
    public static class UserRecipeRes {
        //TODO 사진 정보
        private String title;
    }
}

