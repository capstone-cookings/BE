package com.cook.cookapp.recipe.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

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
        private List<String> ingredients;
        private String imageUrl;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class MyRecipeRes {
        private Long id;
        private String title;
        private boolean isLiked;
        private String imageUrl;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class IsLikedRecipeRes {
        private boolean isLiked;
    }



}

