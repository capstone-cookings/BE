package com.cook.cookapp.recipe.dto.res;

import com.cook.cookapp.user.entity.ProfileImage;
import com.cook.cookapp.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private String email;
    private String nickname;
    private ProfileImage profileImage;

    public UserProfileResponse(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
    }
}
