package com.cook.cookapp.user.dto.res;

import com.cook.cookapp.user.entity.ProfileImage;
import lombok.*;

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

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserProfileRes {
        private String email;
        private String nickname;
//        private ProfileImage profileImage;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TastePreferenceRes {
        private String tastePreference;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserLocationRes {
        private String district;
        private String neighborhood;
    }

}
