package com.cook.cookapp.user.dto.res;

import com.cook.cookapp.user.entity.ProfileImage;
import com.cook.cookapp.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private String email;
    private String nickname;
    private ProfileImage profileImage;

    public static UserProfileResponse fromUser(User user) {
        return UserProfileResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }

}
