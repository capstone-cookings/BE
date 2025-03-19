package com.cook.cookapp.user.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class UserDtoRes {

    @Data
    @AllArgsConstructor
    @Builder
    public static class UserLoginRes {
        private Long id;
        private String email;
        private String accessToken;
        private String refreshToken;
        private String nickname;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class UserRecipeRes {
        private Long id;
        private String title;
        private String instructions;
    }
}
