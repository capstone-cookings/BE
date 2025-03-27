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
        private Long id;
        private String title;
        private boolean isLiked;
        private String instructions;
    }
}

