package com.cook.cookapp.user.converter;

import com.cook.cookapp.user.dto.res.UserDtoRes;
import com.cook.cookapp.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public static UserDtoRes.UserLoginRes signInRes(User user, String accessToken, String refreshToken, String nickname) {
        return UserDtoRes.UserLoginRes.builder()
                .id(user.getId())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(nickname)
                .build();
    }

    public static UserDtoRes.UserProfileRes userProfileRes(User user) {
        return UserDtoRes.UserProfileRes.builder()
                //TODO 이미지도 해야함
                .email(user.getEmail())
                .nickname(user.getNickname())
//                .profileImage(user.getProfileImage())
                .build();
    }
}
